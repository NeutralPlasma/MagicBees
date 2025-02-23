package eu.virtusdevelops.magicbees.plugin.listeners

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class BlockInteractListener(private val beeHiveController: BeeHiveController) : Listener{


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockInteract(event: PlayerInteractEvent){
        if(event.clickedBlock == null) return
        val blockLocation = event.clickedBlock?.location ?: return

        val hive = beeHiveController.getBeehive(blockLocation) ?: return

        // process the click

    }


}