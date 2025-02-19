package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.Provider
import org.bukkit.entity.Player

class BasicIntegerReward(
    private val min: Int,
    private val max: Int,
    private val provider: Provider<Int>
) : Reward {
    override fun give(player: Player) {
        val amount = (min..max).random()
        provider.give(player, amount)
    }
}