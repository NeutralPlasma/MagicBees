package eu.virtusdevelops.magicbees.api.rewards

import org.bukkit.entity.Player

interface Reward {


    fun give(player: Player)


    fun getName(): String

    fun getMin(): Double

    fun getMax(): Double

}