package eu.virtusdevelops.magicbees.api

import org.bukkit.entity.Player

interface Provider<T> {

    fun init()

    fun isInitialized(): Boolean

    fun getName(): String

    fun give(player: Player, value: T): Boolean

    fun take(player: Player, value: T): Boolean

    fun has(player: Player, value: T): Boolean

    fun set(player: Player, value: T): Boolean
}