package eu.virtusdevelops.magicbees.api

import org.bukkit.entity.Player

interface AdvancedProvider<T, B>: Provider<T> {


    fun give(player: Player, value: T, amount: B): Boolean

    fun take(player: Player, value: T, amount: B): Boolean

    fun has(player: Player, value: T, amount: B): Boolean

    fun set(player: Player, value: T, amount: B): Boolean

}