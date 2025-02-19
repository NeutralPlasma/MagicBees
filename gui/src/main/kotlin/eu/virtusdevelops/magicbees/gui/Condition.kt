package eu.virtusdevelops.magicbees.gui

import org.bukkit.entity.Player

interface Condition {

    fun check(player: Player, icon: Icon): Boolean
}