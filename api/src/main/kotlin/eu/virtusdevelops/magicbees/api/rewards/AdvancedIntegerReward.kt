package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import org.bukkit.entity.Player
import kotlin.random.Random

class AdvancedIntegerReward(
    private val itemName: String,
    private val min: Int,
    private val max: Int,
    private val provider: AdvancedProvider<String, Int>
) : Reward {
    override fun give(player: Player) {
        val amount = Random.nextInt(min, max)
        provider.give(player, itemName, amount)
    }
}