package eu.virtusdevelops.magicbees.plugin.listeners

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.models.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class BlockInteractListener(private val beeHiveController: BeeHiveController) : Listener{


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockInteract(event: PlayerInteractEvent){
        if(event.clickedBlock == null) return
        val blockLocation = event.clickedBlock?.location ?: return

        val location = Location(blockLocation.blockX, blockLocation.blockY, blockLocation.blockZ, blockLocation.world.name)

        val hive = beeHiveController.getBeehive(location) ?: return

        // process the click

    }


}