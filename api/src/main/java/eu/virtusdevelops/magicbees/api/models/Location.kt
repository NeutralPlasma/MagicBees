package eu.virtusdevelops.magicbees.api.models

import org.bukkit.Bukkit
import org.bukkit.World

data class Location(
    val x: Int,
    val y: Int,
    val z: Int,
    val worldName: String
){
    fun getWorld(): World? = lazy { Bukkit.getWorld(worldName) }.value
}
