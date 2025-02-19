package eu.virtusdevelops.magicbees.core.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.rewards.AdvancedDoubleReward
import eu.virtusdevelops.magicbees.api.rewards.AdvancedIntegerReward
import eu.virtusdevelops.magicbees.api.rewards.Reward
import eu.virtusdevelops.magicbees.api.rewards.RewardParser

class CoinsEngineRewardParser : RewardParser {
    override fun getName(): String {
        return "CoinsEngine"
    }

    override fun parseReward(data: String): Reward {
        val split = data.split(":")
        if(split.size != 4) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider(getName())
            ?: throw IllegalStateException("Item provider is not present!")

        if(provider !is AdvancedProvider<*, *>) throw IllegalArgumentException("Invalid provider!")

        val minAmount = split[2].toDouble()
        val maxAmount = split[3].toDouble()
        val currency = split[1]


        return AdvancedDoubleReward(currency, minAmount, maxAmount, provider as AdvancedProvider<String, Double>)
    }
}