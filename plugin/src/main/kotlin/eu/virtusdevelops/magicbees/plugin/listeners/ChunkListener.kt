package eu.virtusdevelops.magicbees.plugin.listeners

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.models.ChunkLocation
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent

class ChunkListener(
    private val beehiveController: BeeHiveController
) : Listener {


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onChunkLoad(event: ChunkLoadEvent) {
        val chunk = event.chunk
        val x = chunk.x
        val z = chunk.z

        beehiveController.loadChunk(ChunkLocation(x, z), event.chunk.world.name)
    }



    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onChunkUnload(event: ChunkUnloadEvent) {
        val chunk = event.chunk
        val x = chunk.x
        val z = chunk.z

        beehiveController.unloadChunk(ChunkLocation(x, z), event.chunk.world.name)
    }

}