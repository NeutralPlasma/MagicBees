package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.Provider
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class VaultProvider : Provider<Double> {
    private var initialized: Boolean = false
    private lateinit var economy: Economy


    override fun init() {
        if(initialized) return
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) {
            return
        }

        val rsp = Bukkit.getServer().servicesManager.getRegistration(
            Economy::class.java
        )

        if (rsp == null) {
            return
        }

        economy = rsp.provider
        initialized = true
    }

    override fun getName(): String {
        return "Vault"
    }

    override fun isInitialized(): Boolean {
        return initialized
    }

    override fun set(player: Player, value: Double): Boolean {
        val balance = economy.getBalance(player)
        if(economy.withdrawPlayer(player, balance).transactionSuccess())
            return economy.depositPlayer(player, value).transactionSuccess()
        return false
    }

    override fun has(player: Player, value: Double): Boolean {
        val balance = economy.getBalance(player)
        return balance >= value
    }

    override fun take(player: Player, value: Double): Boolean {
        return economy.withdrawPlayer(player, value).transactionSuccess()
    }

    override fun give(player: Player, value: Double): Boolean {
        return economy.depositPlayer(player, value).transactionSuccess()
    }
}