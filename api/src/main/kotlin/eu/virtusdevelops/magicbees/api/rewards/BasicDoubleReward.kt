package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.Provider
import org.bukkit.entity.Player
import kotlin.random.Random

class BasicDoubleReward(
    private val min: Double,
    private val max: Double,
    private val provider: Provider<Double>
) : Reward {
    override fun give(player: Player) {
        val amount = Random.nextDouble(min, max)
        provider.give(player, amount)
    }
}