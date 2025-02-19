package eu.virtusdevelops.magicbees.core.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.api.rewards.*

class VaultRewardParser : RewardParser {
    override fun getName(): String {
        return "Vault"
    }

    override fun parseReward(data: String): Reward {
        val split = data.split(":")
        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider(getName())
            ?: throw IllegalStateException("Item provider is not present!")


        val minAmount = split[1].toDouble()
        val maxAmount = split[2].toDouble()


        return BasicDoubleReward(minAmount, maxAmount, provider as Provider<Double>)
    }
}