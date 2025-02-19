package eu.virtusdevelops.magicbees.gui

import org.bukkit.entity.Player

fun interface Action {
    fun execute(player: Player)
}