package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import su.nightexpress.coinsengine.api.CoinsEngineAPI

class CoinsEngineProvider : AdvancedProvider<String, Double> {


    override fun init() {

    }

    override fun isInitialized(): Boolean {
        return Bukkit.getPluginManager().isPluginEnabled("CoinsEngine")
    }

    override fun getName(): String {
        return "CoinsEngine"
    }


    override fun give(player: Player, value: String, amount: Double): Boolean {
        val currency = CoinsEngineAPI.getCurrency(value) ?: return false
        CoinsEngineAPI.addBalance(player, currency, amount)
        return true
    }

    override fun give(player: Player, value: String): Boolean {
        return give(player, value, 1.0)
    }

    override fun take(player: Player, value: String, amount: Double): Boolean {
        val currency = CoinsEngineAPI.getCurrency(value) ?: return false
        val balance = CoinsEngineAPI.getBalance(player, currency)
        if(balance < amount) return false
        CoinsEngineAPI.removeBalance(player, currency, amount)
        return true
    }

    override fun take(player: Player, value: String): Boolean {
        return take(player, value, 1.0)
    }

    override fun has(player: Player, value: String, amount: Double): Boolean {
        val currency = CoinsEngineAPI.getCurrency(value) ?: return false
        val balance = CoinsEngineAPI.getBalance(player, currency)
        return balance >= amount

    }

    override fun has(player: Player, value: String): Boolean {
        return has(player, value, 1.0)
    }

    override fun set(player: Player, value: String, amount: Double): Boolean {
        val currency = CoinsEngineAPI.getCurrency(value) ?: return false
        CoinsEngineAPI.setBalance(player, currency, amount)
        return true
    }

    override fun set(player: Player, value: String): Boolean {
        return set(player, value, 1.0)
    }


}