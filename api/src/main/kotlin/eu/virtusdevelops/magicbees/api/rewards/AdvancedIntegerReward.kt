package eu.virtusdevelops.magicbees.api.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import org.bukkit.entity.Player
import kotlin.random.Random

class AdvancedIntegerReward(
    private val type: String,
    private val min: Int,
    private val max: Int,
    private val provider: AdvancedProvider<String, Int>
) : Reward {
    override fun give(player: Player) {
        val amount = Random.nextInt(min, max+1)
        provider.give(player, type, amount)
    }


    override fun getName(): String {
        return MagicBeesAPI.get()?.getTranslationsController()?.getString(type.uppercase()) ?: type
    }

    override fun getMin(): Double {
        return min.toDouble()
    }

    override fun getMax(): Double {
        return max.toDouble()
    }


}