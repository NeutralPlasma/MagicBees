package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import org.bukkit.entity.Player
import kotlin.random.Random

class AdvancedDoubleReward(
    private val type: String,
    private val min: Double,
    private val max: Double,
    private val provider: AdvancedProvider<String, Double>
) : Reward {
    override fun give(player: Player) {
        if(min > max) throw IllegalArgumentException()
        if(min == max) {
            provider.give(player, type , min)
            return
        }
        val amount = Random.nextDouble(min, max)
        provider.give(player, type, amount)
    }

    override fun getName(): String {
        return MagicBeesAPI.get()?.getTranslationsController()?.getString(type.uppercase()) ?: type
    }

    override fun getMin(): Double {
        return min
    }

    override fun getMax(): Double {
        return max
    }

}