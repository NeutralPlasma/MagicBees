package eu.virtusdevelops.magicbees.plugin.listeners

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.models.Location
import org.bukkit.Material
import org.bukkit.entity.Bee
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityEnterBlockEvent

class BeeInteractListener(private val beeHiveController: BeeHiveController) : Listener {





    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun beeInteractEvent(event: EntityEnterBlockEvent){

        val entity = event.entity
        if(entity !is Bee) return

        val block = event.block
        if (block.type != Material.BEEHIVE) return

        val blockLocation = event.block.location
        val location = Location(blockLocation.blockX, blockLocation.blockY, blockLocation.blockZ, blockLocation.world.name)

        val hive = beeHiveController.getBeehive(location) ?: return

        // do the check whatever thing to check if bee entered the hive (so hive now has more bees or something?)

    }
}