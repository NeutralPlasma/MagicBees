package eu.virtusdevelops.magicbees.gui

import org.bukkit.entity.Player

fun interface UpdateAction {
    fun update(player: Player, icon: Icon)
}