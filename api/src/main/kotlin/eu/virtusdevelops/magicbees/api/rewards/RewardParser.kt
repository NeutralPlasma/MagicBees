package eu.virtusdevelops.magicbees.api.rewards

interface RewardParser {
    fun getName(): String
    fun parseReward(data: String): Reward
}