package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.Provider
import org.bukkit.entity.Player
import kotlin.random.Random

class BasicIntegerReward(
    private val min: Int,
    private val max: Int,
    private val provider: Provider<Int>
) : Reward {
    override fun give(player: Player) {
        val amount = Random.nextInt(min, max+1)
        provider.give(player, amount)
    }
}