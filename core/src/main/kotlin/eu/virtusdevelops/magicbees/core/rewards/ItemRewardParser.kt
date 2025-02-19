package eu.virtusdevelops.magicbees.core.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.rewards.AdvancedIntegerReward
import eu.virtusdevelops.magicbees.api.rewards.Reward
import eu.virtusdevelops.magicbees.api.rewards.RewardParser

class ItemRewardParser : RewardParser {
    override fun getName(): String {
        return "Item"
    }

    override fun parseReward(data: String): Reward {
        val split = data.split(":")
        if(split.size != 4) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider(getName())
            ?: throw IllegalStateException("Item provider is not present!")

        if(provider !is AdvancedProvider<*, *>) throw IllegalArgumentException("Invalid provider!")

        val minAmount = split[2].toInt()
        val maxAmount = split[3].toInt()
        val itemName = split[1]


        return AdvancedIntegerReward(itemName, minAmount, maxAmount, provider as AdvancedProvider<String, Int>)
    }
}