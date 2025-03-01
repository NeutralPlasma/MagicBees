package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class BasicCommandReward(
    private val command: String,
    private val min: Int,
    private val max: Int,
) : Reward {




    override fun give(player: Player) {
        val random = (min..max).random()
        val commandSender = Bukkit.getConsoleSender()
        val actualCommand = command.replace("%player%", player.name).replace("%amount%", random.toString())
        Bukkit.dispatchCommand(commandSender, actualCommand)
    }

    override fun getName(): String {
        val key = "Command:$command"
        return MagicBeesAPI.get()?.getTranslationsController()?.getString(key.uppercase()) ?: key
    }

    override fun getMin(): Double {
        return min.toDouble()
    }

    override fun getMax(): Double {
        return max.toDouble()
    }
}