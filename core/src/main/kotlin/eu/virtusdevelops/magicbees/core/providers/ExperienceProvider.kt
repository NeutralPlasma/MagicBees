package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.core.utils.ExperienceUtils
import org.bukkit.entity.Player

class ExperienceProvider : Provider<Int> {
    private var initialized: Boolean = false


    override fun init() {
        if(initialized) return
        initialized = true
    }

    override fun isInitialized(): Boolean {
        return initialized
    }

    override fun getName(): String {
        return "Experience"
    }

    override fun set(player: Player, value: Int): Boolean {
        ExperienceUtils.setExp(player, value)
        return true
    }

    override fun has(player: Player, value: Int): Boolean {
        return ExperienceUtils.getExp(player) >= value
    }

    override fun take(player: Player, value: Int): Boolean {
        if(!has(player, value))
            return false
        ExperienceUtils.addExp(player, -value)
        return true
    }

    override fun give(player: Player, value: Int): Boolean {
        ExperienceUtils.addExp(player, value)
        return true
    }
}