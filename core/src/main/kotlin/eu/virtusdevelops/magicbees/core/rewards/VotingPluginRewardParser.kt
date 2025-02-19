package eu.virtusdevelops.magicbees.core.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.api.rewards.AdvancedIntegerReward
import eu.virtusdevelops.magicbees.api.rewards.BasicIntegerReward
import eu.virtusdevelops.magicbees.api.rewards.Reward
import eu.virtusdevelops.magicbees.api.rewards.RewardParser

class VotingPluginRewardParser : RewardParser {
    override fun getName(): String {
        return "VotingPlugin"
    }

    override fun parseReward(data: String): Reward {
        val split = data.split(":")
        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider(getName())
            ?: throw IllegalStateException("Item provider is not present!")

        val minAmount = split[1].toInt()
        val maxAmount = split[2].toInt()

        return BasicIntegerReward(minAmount, maxAmount, provider as Provider<Int>)
    }
}