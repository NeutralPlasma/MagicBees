package eu.virtusdevelops.magicbees.core.storage

import eu.virtusdevelops.magicbees.api.models.BeeHive
import java.util.UUID

interface BeeHiveDao: CrudDao<BeeHive, UUID>{
    fun getChunkData(chunkX: Int, chunkZ: Int, world: String): List<BeeHive>
}