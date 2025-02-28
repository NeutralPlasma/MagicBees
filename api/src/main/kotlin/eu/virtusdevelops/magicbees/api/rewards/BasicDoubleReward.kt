package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.Provider
import org.bukkit.entity.Player
import kotlin.random.Random

class BasicDoubleReward(
    private val min: Double,
    private val max: Double,
    private val provider: Provider<Double>
) : Reward {
    override fun give(player: Player) {
        if(min > max) throw IllegalArgumentException()
        if(min == max) {
            provider.give(player, min)
            return
        }
        val amount = Random.nextDouble(min, max)
        provider.give(player, amount)
    }


    override fun getName(): String {
        return MagicBeesAPI.get()?.getTranslationsController()?.getString(provider.getName().uppercase()) ?: provider.getName()
    }

    override fun getMin(): Double {
        return min
    }

    override fun getMax(): Double {
        return max
    }


}