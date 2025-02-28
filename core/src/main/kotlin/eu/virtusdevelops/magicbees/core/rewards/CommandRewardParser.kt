package eu.virtusdevelops.magicbees.core.rewards

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.rewards.*

class CommandRewardParser : RewardParser {
    override fun getName(): String {
        return "Command"
    }

    override fun parseReward(data: String): Reward {
        val split = data.split(":")
        if(split.size != 4) throw IllegalArgumentException("Invalid data format!")

        val minAmount = split[2].toInt()
        val maxAmount = split[3].toInt()
        val command = split[1]


        return BasicCommandReward(command, minAmount, maxAmount)
    }
}