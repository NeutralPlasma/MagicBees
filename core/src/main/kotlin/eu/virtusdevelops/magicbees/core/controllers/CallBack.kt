package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.core.storage.ChunkData

fun interface CallBack {
    fun run(data: ChunkData<BeeHive>)
}