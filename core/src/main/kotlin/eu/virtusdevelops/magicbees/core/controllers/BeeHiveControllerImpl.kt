package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.RequirementsController
import eu.virtusdevelops.magicbees.api.controllers.RewardsController
import eu.virtusdevelops.magicbees.api.models.*
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import eu.virtusdevelops.magicbees.core.utils.NBTUtil
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.Material
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

class BeeHiveControllerImpl(
    private val plugin: JavaPlugin,
    private val levelsStorage: FileStorage,
    private val requirementsController: RequirementsController,
    private val rewardsController: RewardsController,
    private val storageController: StorageController,
    private val logger: Logger) : BeeHiveController {

    private val beeHiveStorage = storageController.getBeeHiveStorage()
    private val beeHiveLevels: HashMap<Int, BeeHiveLevel> = HashMap()





    override fun initialize(): Boolean {
        // load all beehives
        beeHiveLevels.clear()

        // load levels from filestorage
        levelsStorage.loadData()
        val config = levelsStorage.getConfiguration()!!
        config.getKeys(false).forEach { key ->
            val level = parseHoneyLevel(config.getConfigurationSection(key)!!)
            if(level != null)
                beeHiveLevels[level.level] = level
        }

        return true
    }

    override fun saveBeeHive(beeHive: BeeHive) {
        beeHiveStorage.store(beeHive)
    }

    override fun removeBeeHive(beeHive: BeeHive) {
        beeHiveStorage.remove(beeHive)
    }

    override fun getBeehive(location: Location): BeeHive? {
        return beeHiveStorage.get(location)
    }

    override fun canHarvest(player: Player, beeHive: BeeHive): Result<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        if(beeHive.fullnessStatus == 0){
            errors.add(MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.HIVE_EMPTY) ?: Messages.HIVE_EMPTY.toString())
            return Result.failure(errors)
        }
        if(!beeHiveLevels.containsKey(beeHive.honeyUpgradeLevel)){
            errors.add("Missing configuration for honey comb level ${beeHive.honeyUpgradeLevel}!")
            return Result.failure(errors)
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
            }
        }
        if(passed)
            return Result.success(true)
        return Result.failure(errors)
    }

    override fun canComb(player: Player, beeHive: BeeHive): Result<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        if(beeHive.fullnessStatus == 0){
            errors.add(MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.HIVE_EMPTY) ?: Messages.HIVE_EMPTY.toString())
            return Result.failure(errors)
        }
        if(!beeHiveLevels.containsKey(beeHive.honeyCombUpgradeLevel)){
            errors.add("Missing configuration for honey comb level ${beeHive.honeyCombUpgradeLevel}!")
            return Result.failure(errors)
        }
        val levelData = beeHiveLevels[beeHive.honeyCombUpgradeLevel]!!
        val requirements = requirementsController.getRequirements(levelData.combRequirements)
        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                // send message
                errors.add(
                    MagicBeesAPI.get()?.getTranslationsController()?.getString(Messages.MISSING_REQUIREMENT, it.getName(), it.getType()) ?:
                    "${Messages.MISSING_REQUIREMENT} ${it.getName()} ${it.getType()}"
                )
            }
        }
        if(passed)
            return Result.success(true)
        return Result.failure(errors)
    }

    // can upgrade honey
    override fun canUpgradeHoneyLevel(player: Player, beeHive: BeeHive): Result<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        val nextLevel = beeHive.honeyUpgradeLevel + 1
        if(!beeHiveLevels.containsKey(nextLevel)){
            errors.add("Missing configuration for honey comb level ${beeHive.honeyUpgradeLevel}!")
            return Result.failure(errors)
        }
        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.honeyUpgradeRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                errors.add(
                    MagicBeesAPI.get()?.getTranslationsController()
                        ?.getString(Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
                        ?: (Messages.MISSING_REQUIREMENT.name + ":" + it.getName() + ":" + it.getType())
                )
            }
        }
        if(!passed)
            return Result.failure(errors)
        return Result.success(true)
    }

    // can upgrade comb
    override fun canUpgradeCombLevel(player: Player, beeHive: BeeHive): Result<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        val nextLevel = beeHive.honeyCombUpgradeLevel + 1
        if(!beeHiveLevels.containsKey(nextLevel)){
            errors.add("Missing configuration for honey comb level ${beeHive.honeyCombUpgradeLevel}!")
            return Result.failure(errors)
        }
        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.combUpgradeRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                errors.add(
                    MagicBeesAPI.get()?.getTranslationsController()
                        ?.getString(Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
                        ?: (Messages.MISSING_REQUIREMENT.name + ":" + it.getName() + ":" + it.getType())
                )
            }
        }
        if(!passed)
            return Result.failure(errors)
        return Result.success(true)
    }

    override fun harvestBeeHive(player: Player, beeHive: BeeHive): Boolean {

        val result = canHarvest(player, beeHive)
        if(result is Result.Failure){
            result.errors.forEach{
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

        val rewards = rewardsController.getRewards(levelData.harvestRewards)
        rewards.forEach {
            it.give(player)
        }

        return true
    }

    override fun combBeeHive(player: Player, beeHive: BeeHive): Boolean {

        val result = canComb(player, beeHive)
        if(result is Result.Failure){
            result.errors.forEach{
                player.sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
            return false
        }

        val levelData = beeHiveLevels[beeHive.honeyCombUpgradeLevel]!!
        val requirements = requirementsController.getRequirements(levelData.combRequirements)

        requirements.forEach {
            it.processRequirement(player)
        }

        beeHive.combCollectedTimes += 1
        beeHive.fullnessStatus = 0
        beeHive.lastCollectionTime = System.currentTimeMillis()
        MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.SUCCESSFUL_HARVEST)

        val rewards = rewardsController.getRewards(levelData.combRewards)
        rewards.forEach {
            it.give(player)
        }

        return true

    }



    override fun upgradeHoneyLevel(player: Player, beeHive: BeeHive): Boolean {
        val nextLevel = beeHive.honeyUpgradeLevel + 1
        val result = canUpgradeHoneyLevel(player, beeHive)
        if(result is Result.Failure){
            result.errors.forEach{
                player.sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
            return false
        }

        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.honeyUpgradeRequirements)

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
            result.errors.forEach{
                player.sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
            return false
        }

        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.combUpgradeRequirements)

        requirements.forEach {
            it.processRequirement(player)
        }
        beeHive.honeyCombUpgradeLevel += 1

        saveBeeHive(beeHive)
        // send upgrade message
        return true
    }

    override fun loadChunk(chunk: ChunkLocation, world: String) {
        beeHiveStorage.loadChunkAsync(chunk, world)
    }

    override fun unloadChunk(chunk: ChunkLocation, world: String) {
        beeHiveStorage.unloadChunk(chunk, world)
    }

    override fun getBeeHiveItem(honeyLevel: Int, combLevel: Int): ItemStack {
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

        return itemStack
    }


    fun parseHoneyLevel(data: ConfigurationSection): BeeHiveLevel? {
        try{
            val level = data.name.toInt()
            val harvestRequirements = data.getStringList("harvest_requirements")
            val harvestRewards = data.getStringList("harvest_rewards")
            val combRequirements = data.getStringList("comb_requirements")
            val combRewards = data.getStringList("comb_rewards")
            val honeyUpgradeRequirements = data.getStringList("honey_upgrade_requirements")
            val combUpgradeRequirements = data.getStringList("comb_upgrade_requirements")


            return BeeHiveLevel(
                level,
                harvestRequirements,
                combRequirements,
                honeyUpgradeRequirements,
                combUpgradeRequirements,
                harvestRewards,
                combRewards
            )

        }catch (e: Exception){
            return null
        }
    }


}