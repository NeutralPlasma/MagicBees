package eu.virtusdevelops.magicbees.api.models

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

data class HiveLocation(
    val chunkLocation: ChunkLocation,
    val position: Position,
    val worldName: String
){
    /**
     * Lazily initialized Minecraft world instance corresponding to the `worldName`.
     * This property references the server's World object determined by the `worldName` associated
     * with this instance of CropPosition.
     *
     * @throws IllegalArgumentException if the world identified by `worldName` does not exist.
     */
    val world by lazy { Bukkit.getWorld(worldName) }

    val bukkitLocation by lazy {
        val world = Bukkit.getWorld(worldName)
            ?: throw IllegalArgumentException("World $worldName does not exist!")

        val x = chunkLocation.x.shl(4) + position.x
        val z = chunkLocation.z.shl(4) + position.z

        Location(world, x.toDouble(), position.z.toDouble(),  z.toDouble())
    }




    companion object {
        fun fromBukkitLocation(location: Location): HiveLocation {
            val worldName = location.world.name
            val chunkX = location.blockX.shr(4)
            val chunkZ = location.blockZ.shr(4)
            val position = Position(location.blockX.rem(16), location.blockY, location.blockZ.rem(16))

            return HiveLocation(ChunkLocation(chunkX, chunkZ), position, worldName)
        }
    }
}
