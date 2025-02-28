package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.RequirementsController
import eu.virtusdevelops.magicbees.api.controllers.RewardsController
import eu.virtusdevelops.magicbees.api.models.*
import eu.virtusdevelops.magicbees.api.rewards.Reward
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.ChunkData
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import eu.virtusdevelops.magicbees.core.utils.NBTUtil
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Beehive
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger
import kotlin.collections.HashMap

class BeeHiveControllerImpl(
    private val plugin: JavaPlugin,
    private val levelsStorage: FileStorage,
    private val requirementsController: RequirementsController,
    private val rewardsController: RewardsController,
    private val storageController: StorageController,
    private val logger: Logger) : BeeHiveController {

    private val beeHiveStorage = storageController.getBeeHiveStorage()
    private val beeHiveLevels: HashMap<Int, BeeHiveLevel> = HashMap()
    private val combHiveLevels: HashMap<Int, BeeHiveLevel> = HashMap()

    private var chunkLoadStatus: HashMap<String, HashMap<ChunkLocation, Long>> = HashMap()


    override fun init(): Boolean {
        // load all beehives
        logger.info("Initializing beehive controller...")
        reload()
        return true
    }

    override fun reload() {
        beeHiveLevels.clear()
        levelsStorage.loadData()
        val config = levelsStorage.getConfiguration()!!

        val honeyLevelsSection = config.getConfigurationSection("honey_levels")!!

        honeyLevelsSection.getKeys(false).forEach { key ->
            val level = parseLevel(honeyLevelsSection.getConfigurationSection(key)!!)
            if(level != null)
                beeHiveLevels[level.level] = level
        }

        val combLevelsSection = config.getConfigurationSection("comb_levels")!!
        combLevelsSection.getKeys(false).forEach { key ->
            val level = parseLevel(combLevelsSection.getConfigurationSection(key)!!)
            if(level != null)
                combHiveLevels[level.level] = level
        }
    }


    override fun saveBeeHive(beeHive: BeeHive): Boolean {
        return beeHiveStorage.store(beeHive)
    }

    override fun removeBeeHive(beeHive: BeeHive): Boolean {
        return beeHiveStorage.remove(beeHive)
    }

    override fun getBeehive(location: Location): BeeHive? {
        return beeHiveStorage.get(location)
    }

    override fun canHarvest(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>> {
        val errors = mutableListOf<String>()
        val success = mutableListOf<String>()
        if(beeHive.fullnessStatus != 5){
            errors.add(MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.HIVE_EMPTY) ?: Messages.HIVE_EMPTY.toString())
            return Result.failure(ListResult(success, errors))
        }
        if(!beeHiveLevels.containsKey(beeHive.honeyUpgradeLevel)){
            errors.add(MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.BEE_HIVE_MAX_LEVEL) ?: "MAX_LEVEL")
            return Result.failure(ListResult(success, errors))
        }
        val levelData = beeHiveLevels[beeHive.honeyUpgradeLevel]!!
        val requirements = requirementsController.getRequirements(levelData.harvestRequirements)
        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                // send message
                errors.add(
                    MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.MISSING_REQUIREMENT, it.getName(), it.getType()) ?:
                    "${Messages.MISSING_REQUIREMENT} ${it.getName()} ${it.getType()}"
                )
            }else{
                success.add(
                    MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.PASSED_REQUIREMENT, it.getName(), it.getType()) ?:
                    "${Messages.MISSING_REQUIREMENT} ${it.getName()} ${it.getType()}"
                )
            }
        }
        if(passed)
            return Result.success(success)
        return Result.failure(ListResult(success, errors))
    }

    override fun canComb(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>> {
        val errors = mutableListOf<String>()
        val success = mutableListOf<String>()
        if(beeHive.fullnessStatus != 5){
            errors.add(MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.HIVE_EMPTY) ?: Messages.HIVE_EMPTY.toString())
            return Result.failure(ListResult(success, errors))
        }
        if(!combHiveLevels.containsKey(beeHive.honeyCombUpgradeLevel)){
            errors.add("Missing configuration for honey comb level ${beeHive.honeyCombUpgradeLevel}!")
            return Result.failure(ListResult(success, errors))
        }
        val levelData = combHiveLevels[beeHive.honeyCombUpgradeLevel]!!
        val requirements = requirementsController.getRequirements(levelData.harvestRequirements)
        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                // send message
                errors.add(
                    MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.MISSING_REQUIREMENT, it.getName(), it.getType()) ?:
                    "${Messages.MISSING_REQUIREMENT} ${it.getName()} ${it.getType()}"
                )
            }else{
                success.add(
                    MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.PASSED_REQUIREMENT, it.getName(), it.getType()) ?:
                    "${Messages.MISSING_REQUIREMENT} ${it.getName()} ${it.getType()}"
                )
            }
        }
        if(passed)
            return Result.success(success)
        return Result.failure(ListResult(success, errors))
    }

    // can upgrade honey
    override fun canUpgradeHoneyLevel(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>> {
        val errors = mutableListOf<String>()
        val success = mutableListOf<String>()
        val nextLevel = beeHive.honeyUpgradeLevel + 1
        if(!beeHiveLevels.containsKey(nextLevel)){
            errors.add(MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.BEE_HIVE_MAX_LEVEL) ?: "MAX_LEVEL")
            return Result.other(errors)
        }
        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.upgradeRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                errors.add(
                    MagicBeesAPI.get()?.getTranslationsController()
                        ?.getString(Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
                        ?: (Messages.MISSING_REQUIREMENT.name + ":" + it.getName() + ":" + it.getType())
                )
            }else{
                success.add(
                    MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.PASSED_REQUIREMENT, it.getName(), it.getType()) ?:
                    "${Messages.MISSING_REQUIREMENT} ${it.getName()} ${it.getType()}"
                )
            }
        }
        if(!passed)
            return Result.failure(ListResult(success, errors))
        return Result.success(success)
    }

    // can upgrade comb
    override fun canUpgradeCombLevel(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>> {
        val errors = mutableListOf<String>()
        val success = mutableListOf<String>()
        val nextLevel = beeHive.honeyCombUpgradeLevel + 1
        if(!combHiveLevels.containsKey(nextLevel)){
            errors.add(MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.BEE_HIVE_MAX_LEVEL) ?: "MAX_LEVEL")
            return Result.other(errors)
        }
        val levelData = combHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.upgradeRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                errors.add(
                    MagicBeesAPI.get()?.getTranslationsController()
                        ?.getString(Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
                        ?: (Messages.MISSING_REQUIREMENT.name + ":" + it.getName() + ":" + it.getType())
                )
            }else{
                success.add(
                    MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.PASSED_REQUIREMENT, it.getName(), it.getType()) ?:
                    "${Messages.MISSING_REQUIREMENT} ${it.getName()} ${it.getType()}"
                )
            }
        }
        if(!passed)
            return Result.failure(ListResult(success, errors))
        return Result.success(success)
    }

    override fun harvestBeeHive(player: Player, beeHive: BeeHive): Boolean {

        val result = canHarvest(player, beeHive)
        if(result is Result.Failure){
            result.errors.failed.forEach{
                player.sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
            return false
        }
        val levelData = beeHiveLevels[beeHive.honeyUpgradeLevel]!!

        val requirements = requirementsController.getRequirements(levelData.harvestRequirements)


        requirements.forEach {
            it.processRequirement(player)
        }

        beeHive.honeyCollectedTimes += 1
        beeHive.fullnessStatus = 0
        beeHive.lastCollectionTime = System.currentTimeMillis()
        MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.SUCCESSFUL_HARVEST)


        NBTUtil.setHoneyLevel(beeHive.location.block, 0)


        val rewards = rewardsController.getRewards(levelData.harvestRewards)
        rewards.forEach {
            it.give(player)
        }

        return true
    }

    override fun combBeeHive(player: Player, beeHive: BeeHive): Boolean {

        val result = canComb(player, beeHive)
        if(result is Result.Failure){
            result.errors.failed.forEach{
                player.sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
            return false
        }

        val levelData = combHiveLevels[beeHive.honeyCombUpgradeLevel]!!
        val requirements = requirementsController.getRequirements(levelData.harvestRequirements)

        requirements.forEach {
            it.processRequirement(player)
        }

        beeHive.combCollectedTimes += 1
        beeHive.fullnessStatus = 0
        beeHive.lastCollectionTime = System.currentTimeMillis()
        MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.SUCCESSFUL_HARVEST)

        NBTUtil.setHoneyLevel(beeHive.location.block, 0)

        val rewards = rewardsController.getRewards(levelData.harvestRewards)
        rewards.forEach {
            it.give(player)
        }

        return true

    }



    override fun upgradeHoneyLevel(player: Player, beeHive: BeeHive): Boolean {
        val nextLevel = beeHive.honeyUpgradeLevel + 1
        val result = canUpgradeHoneyLevel(player, beeHive)
        if(result is Result.Failure){
            result.errors.failed.forEach{
                player.sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
            return false
        }

        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.upgradeRequirements)

        requirements.forEach {
            it.processRequirement(player)
        }
        beeHive.honeyUpgradeLevel += 1

        saveBeeHive(beeHive)
        // send upgrade message
        return true
    }

    override fun upgradeCombLevel(player: Player, beeHive: BeeHive): Boolean {
        val nextLevel = beeHive.honeyCombUpgradeLevel + 1


        val result = canUpgradeCombLevel(player, beeHive)
        if(result is Result.Failure){
            result.errors.failed.forEach{
                player.sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
            return false
        }

        val levelData = combHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.upgradeRequirements)

        requirements.forEach {
            it.processRequirement(player)
        }
        beeHive.honeyCombUpgradeLevel += 1

        saveBeeHive(beeHive)
        // send upgrade message
        return true
    }

    override fun loadChunk(chunk: ChunkLocation, world: String) {
        beeHiveStorage.loadChunkAsync(chunk, world, (CallBack { chunkData ->

            // offline process chunk
            if(chunkData.items.isEmpty()) return@CallBack

            chunkLoadStatus[world]?.get(chunk)?.let {
                val diff = System.currentTimeMillis() - it
                val minDiff = plugin.config.getInt("offline_beehive_update_interval") * 1000L
                if(diff >= minDiff){
                    updateBeeHives(chunkData.items.values.toList())
                }
            }

        }))

    }

    override fun unloadChunk(chunk: ChunkLocation, world: String) {
        beeHiveStorage.unloadChunk(chunk, world)


        if(!chunkLoadStatus.containsKey(world)){
            chunkLoadStatus[world] = HashMap()
            chunkLoadStatus[world]!![chunk] = System.currentTimeMillis()
        }else{
            chunkLoadStatus[world]!![chunk] = System.currentTimeMillis()
        }
    }

    override fun getBeeHiveItem(honeyLevel: Int, combLevel: Int, beeAmount: Int): ItemStack {
        val itemStack = ItemStack(Material.BEEHIVE)
        val mm = MiniMessage.miniMessage()

        val name = plugin.config.getString("beehive_template.name") ?: ""
        val lore = plugin.config.getStringList("beehive_template.lore")

        val meta = itemStack.itemMeta?.apply {
            itemName(mm.deserialize(name.replace("{honey_level}", honeyLevel.toString()).replace("{comb_level}", combLevel.toString()))
                .decorations(setOf(TextDecoration.ITALIC), false))
            lore(lore.map { mm.deserialize(it.replace("{honey_level}", honeyLevel.toString()).replace("{comb_level}", combLevel.toString()))
                .decorations(setOf(TextDecoration.ITALIC), false) })
            addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        }

        itemStack.itemMeta = meta

        NBTUtil.setNBTTag(itemStack, NBTUtil.COMB_LEVEL_KEY, PersistentDataType.INTEGER, combLevel)
        NBTUtil.setNBTTag(itemStack, NBTUtil.HONEY_LEVEL_KEY, PersistentDataType.INTEGER, honeyLevel)
        NBTUtil.setNBTTag(itemStack, NBTUtil.BEE_AMOUNT, PersistentDataType.INTEGER, beeAmount)

        return itemStack
    }

    override fun getHoneyLevels(): Collection<BeeHiveLevel> {
        return combHiveLevels.values
    }

    override fun getCombLevels(): Collection<BeeHiveLevel> {
        return beeHiveLevels.values
    }

    override fun getHarvestRewards(level: Int): Result<Collection<Reward>, List<String>> {
        val levelData = beeHiveLevels[level] ?: return Result.failure(listOf("Missing configuration for honey level $level!"))
        val rewards = rewardsController.getRewards(levelData.harvestRewards)
        return Result.success(rewards)
    }

    override fun getCombRewards(level: Int): Result<Collection<Reward>, List<String>> {
        val levelData = combHiveLevels[level] ?: return Result.failure(listOf("Missing configuration for comb level $level!"))
        val rewards = rewardsController.getRewards(levelData.harvestRewards)
        return Result.success(rewards)
    }

    fun parseLevel(data: ConfigurationSection): BeeHiveLevel? {
        try{
            val level = data.name.toInt()
            val harvestRequirements = data.getStringList("harvest_requirements")
            val harvestRewards = data.getStringList("harvest_rewards")
            val upgradeRequirements = data.getStringList("upgrade_requirements")

            return BeeHiveLevel(
                level,
                harvestRequirements,
                upgradeRequirements,
                harvestRewards
            )

        }catch (e: Exception){
            return null
        }
    }


    private fun updateBeeHives(beeHives: List<BeeHive>) {
        for (beeHive in beeHives){
            if(beeHive.bees < 1) continue
            val beeHiveBlock = beeHive.location.block
            val data = beeHiveBlock.blockData
            if(data is org.bukkit.block.data.type.Beehive){
                if(data.honeyLevel < data.maximumHoneyLevel){
                    data.honeyLevel = data.maximumHoneyLevel
                }
                beeHiveBlock.blockData = data
                beeHive.fullnessStatus = 5
            }
        }
    }
}