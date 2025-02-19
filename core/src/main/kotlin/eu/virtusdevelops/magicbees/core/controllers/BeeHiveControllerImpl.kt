package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.RequirementsController
import eu.virtusdevelops.magicbees.api.controllers.RewardsController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.BeeHiveLevel
import eu.virtusdevelops.magicbees.api.models.Location
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BeeHiveControllerImpl(
    private val levelsStorage: FileStorage,
    private val dao: BeeHiveDao,
    private val requirementsController: RequirementsController,
    private val rewardsController: RewardsController) : BeeHiveController {

    private val beehives: ConcurrentHashMap<Location, BeeHive> = ConcurrentHashMap()
    private val beeHiveLevels: HashMap<Int, BeeHiveLevel> = HashMap()



    private val executorService: ExecutorService = Executors.newFixedThreadPool(2)

    override fun initialize(): Boolean {
        // load all beehives
        beeHiveLevels.clear()
        beehives.clear()
        try{
            //dao.getAll().forEach { beehives[it.location] = it }
        }catch (e: Exception){
            return false
        }

        // load levels from filestorage
        levelsStorage.loadData()
        val config = levelsStorage.getConfiguration()!!
        config.getKeys(false).forEach { key ->
            val level = parseHoneyLevel(config.getConfigurationSection(key)!!)
            if(level != null)
                beeHiveLevels[level.level] = level
        }

        // temporary
        /*beeHiveLevels[1] = BeeHiveLevel(
            1,
            listOf("Item:gravel:128"),
            listOf(),
            listOf(),
            listOf(),
            listOf("Item:some_magic:1:5"),
            listOf("Item:gravel:1:5"),
        )*/

        val location = Location(0, 0, 0, "world")
        beehives[location] = BeeHive(
            UUID.randomUUID(),
            location,
            UUID.randomUUID(),
            5,
            3,
            1,
            1,
            0,
            0,
            0,
            System.currentTimeMillis(),
            System.currentTimeMillis()
        )

        return true
    }

    override fun saveBeeHive(beeHive: BeeHive) {

        executorService.submit {
            if(!dao.save(beeHive))
                beehives.remove(beeHive.location)
        }

        beehives[beeHive.location] = beeHive
    }

    override fun removeBeeHive(beeHive: BeeHive) {
        executorService.submit {
            if (!dao.delete(beeHive))
                beehives[beeHive.location] = beeHive
        }
        beehives.remove(beeHive.location)
    }

    override fun getBeeHives(): Set<BeeHive> {
        return beehives.values.toSet()
    }

    override fun getPlayerBeehives(playerUUID: UUID): Set<BeeHive> {
        val set: MutableSet<BeeHive> = mutableSetOf()
        for (beehive in beehives) {
            if(beehive.value.owner.equals(playerUUID)) set.add(beehive.value)
        }
        return set;
    }

    override fun getBeehive(beeHiveUUID: UUID): BeeHive? {
        for (beeHive in beehives.values) {
            if(beeHive.id.equals(beeHiveUUID)) return beeHive
        }
        return null
    }

    override fun getBeehive(location: Location): BeeHive? {
        return beehives[location]
    }

    override fun harvestBeeHive(player: Player, beeHive: BeeHive): Boolean {
        if(beeHive.fullnessStatus == 0){
            MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.HIVE_EMPTY)
            return false
        }

        if(!beeHiveLevels.containsKey(beeHive.honeyUpgradeLevel)){
            return false
        }
        val levelData = beeHiveLevels[beeHive.honeyUpgradeLevel]!!

        val requirements = requirementsController.getRequirements(levelData.harvestRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                // send message
                MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
            }
        }
        if(!passed) return false


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
        if(beeHive.fullnessStatus == 0){
            MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.HIVE_EMPTY)
            return false
        }

        if(!beeHiveLevels.containsKey(beeHive.honeyCombUpgradeLevel)){
            return false
        }
        val levelData = beeHiveLevels[beeHive.honeyCombUpgradeLevel]!!

        val requirements = requirementsController.getRequirements(levelData.combRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                // send message
                MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
            }
        }
        if(!passed) return false


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
        if(!beeHiveLevels.containsKey(nextLevel)){
            return false
        }
        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.honeyUpgradeRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
            }
        }
        if(!passed) return false

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
        if(!beeHiveLevels.containsKey(nextLevel)){
            return false
        }
        val levelData = beeHiveLevels[nextLevel]!!
        // requirements
        val requirements = requirementsController.getRequirements(levelData.combUpgradeRequirements)

        var passed = true
        requirements.forEach {
            if(!it.check(player)){
                passed = false
                MagicBeesAPI.get()?.getTranslationsController()?.sendMessage(player, Messages.MISSING_REQUIREMENT, it.getName(), it.getType())
            }
        }
        if(!passed) return false

        requirements.forEach {
            it.processRequirement(player)
        }
        beeHive.honeyCombUpgradeLevel += 1

        saveBeeHive(beeHive)
        // send upgrade message
        return true
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