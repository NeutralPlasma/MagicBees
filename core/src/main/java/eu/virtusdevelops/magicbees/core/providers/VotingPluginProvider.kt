package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.Provider
import org.bukkit.entity.Player
import com.bencodez.votingplugin.VotingPluginMain

class VotingPluginProvider : Provider<Int> {
    private var initialized: Boolean = false

    private lateinit var votingPlugin: VotingPluginMain

    override fun init() {
        votingPlugin = VotingPluginMain.getPlugin()
        initialized = true
    }

    override fun isInitialized(): Boolean {
        return initialized
    }

    override fun getName(): String {
        return "VotingPlugin"
    }

    override fun set(player: Player, value: Int): Boolean {
        votingPlugin.getUser(player.uniqueId).points = value
        return true
    }

    override fun has(player: Player, value: Int): Boolean {
        return votingPlugin.getUser(player.uniqueId).points >= value
    }

    override fun take(player: Player, value: Int): Boolean {
        if(!has(player, value)) {
            return false
        }
        votingPlugin.getUser(player.uniqueId).removePoints(value)
        return true
    }

    override fun give(player: Player, value: Int): Boolean {
        votingPlugin.getUser(player.uniqueId).addPoints(value)
        return true
    }
}