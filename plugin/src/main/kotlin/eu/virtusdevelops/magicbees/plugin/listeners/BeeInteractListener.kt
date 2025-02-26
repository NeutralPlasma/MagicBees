package eu.virtusdevelops.magicbees.plugin.listeners

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import org.bukkit.Material
import org.bukkit.block.EntityBlockStorage
import org.bukkit.block.data.type.Beehive
import org.bukkit.entity.Bee
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityEnterBlockEvent
import org.checkerframework.checker.units.qual.A
import java.util.*
import java.util.logging.Logger
import kotlin.math.log


class BeeInteractListener(
    private val logger: Logger,
    private val beeHiveController: BeeHiveController) : Listener {





    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun beeInteractEvent(event: EntityEnterBlockEvent){

        val entity = event.entity
        if(entity !is Bee) return

        val block = event.block
        val state = block.state
        val data = block.blockData
        if (block.type != Material.BEEHIVE) return

        val hive = beeHiveController.getBeehive(event.block.location) ?: return

        // do the check whatever thing to check if bee entered the hive (so hive now has more bees or something?)
        if(state is org.bukkit.block.Beehive){
            hive.bees = state.entityCount + 1
        }

        if(data is Beehive){
            hive.fullnessStatus = data.honeyLevel
        }



    }
}