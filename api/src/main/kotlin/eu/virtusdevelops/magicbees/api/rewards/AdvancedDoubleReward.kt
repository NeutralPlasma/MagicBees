package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import org.bukkit.entity.Player
import kotlin.random.Random

class AdvancedDoubleReward(
    private val itemName: String,
    private val min: Double,
    private val max: Double,
    private val provider: AdvancedProvider<String, Double>
) : Reward {
    override fun give(player: Player) {
        val amount = Random.nextDouble(min, max)
        provider.give(player, itemName, amount)
    }
}