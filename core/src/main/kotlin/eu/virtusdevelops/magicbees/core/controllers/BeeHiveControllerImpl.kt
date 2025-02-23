package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.RequirementsController
import eu.virtusdevelops.magicbees.api.controllers.RewardsController
import eu.virtusdevelops.magicbees.api.models.*
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

class BeeHiveControllerImpl(
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

    override fun loadChunk(chunk: ChunkLocation, world: String) {
        beeHiveStorage.loadChunkAsync(chunk, world)
    }

    override fun unloadChunk(chunk: ChunkLocation, world: String) {
        beeHiveStorage.unloadChunk(chunk, world)
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