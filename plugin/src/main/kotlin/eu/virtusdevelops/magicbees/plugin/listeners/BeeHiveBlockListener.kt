package eu.virtusdevelops.magicbees.plugin.listeners

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.HiveLocation
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.core.utils.NBTUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Beehive
import org.bukkit.entity.Bee
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.world.PortalCreateEvent.CreateReason
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.*
import java.util.logging.Logger

class BeeHiveBlockListener(
    private val logger: Logger,
    private val beeHiveController: BeeHiveController,
    private val translationsController: TranslationsController) : Listener {



    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBeeHivePlace(event: BlockPlaceEvent){
        val block = event.block
        val material = block.type
        val state = block.state
        if(material != Material.BEEHIVE) return

        val item = event.itemInHand

        val honeyLevel = NBTUtil.getNBTTag(item, NBTUtil.HONEY_LEVEL_KEY, PersistentDataType.INTEGER) ?: return
        // not custom
        val combLevel = NBTUtil.getNBTTag(item, NBTUtil.COMB_LEVEL_KEY, PersistentDataType.INTEGER)
        val beeAmount = NBTUtil.getNBTTag(item, NBTUtil.BEE_AMOUNT, PersistentDataType.INTEGER) ?: 0

        if(!event.player.hasPermission("magicbees.beehive.place")){
            event.isCancelled = true
            translationsController.sendMessage(event.player, Messages.NO_PERMISSION, "magicbees.beehive.place")
            return
        }

        if(state is Beehive){
            for(i in 0 until beeAmount){
                block.world.spawnEntity(block.location.add(Vector(0.5, 1.0, 0.5)), EntityType.BEE, CreatureSpawnEvent.SpawnReason.CUSTOM, {
                    state.addEntity(it as Bee)
                    it.hive = block.location
                    state.update()
                })
            }
        }


        // create new beehive
        val beehive = BeeHive(
            id = UUID.randomUUID(),
            location = HiveLocation.fromBukkitLocation(event.block.location),
            owner = event.player.uniqueId,
            createdTime = System.currentTimeMillis(),
            honeyUpgradeLevel = honeyLevel,
            honeyCombUpgradeLevel = combLevel ?: 0,
            bees = beeAmount
        )
        beeHiveController.saveBeeHive(beehive)
        translationsController.sendMessage(event.player, Messages.BEE_HIVE_PLACED)
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBeeHiveDestroy(event: BlockBreakEvent){
        val block = event.block
        val material = block.type
        val state = block.state
        val player = event.player

        if(material != Material.BEEHIVE) return

        val beeHive = beeHiveController.getBeehive(block.location) ?: return

        if(!player.hasPermission("magicbees.beehive.destroy")){
            event.isCancelled = true
            translationsController.sendMessage(event.player, Messages.NO_PERMISSION, "magicbees.beehive.destroy")
            return
        }

        //
        if(!beeHiveController.removeBeeHive(beeHive)){
            event.isCancelled = true
            logger.warning("Failed removing beehive at location ${beeHive.location}, while player ${player.name} was trying to destroy it")
            return
        }

        // make sure to only get bees that are actually in bee hive
        val data = block.state
        var bees = beeHive.bees
        if(data is Beehive){
            bees = data.entityCount
        }


        val item = beeHiveController.getBeeHiveItem(beeHive.honeyUpgradeLevel, beeHive.honeyCombUpgradeLevel, bees)
        event.isCancelled = true
        block.type = Material.AIR
        // display break particles
        // play sound effect
        // drop item
        block.world.dropItem(block.location, item)
        translationsController.sendMessage(event.player, Messages.BEE_HIVE_BROKEN)
    }
}