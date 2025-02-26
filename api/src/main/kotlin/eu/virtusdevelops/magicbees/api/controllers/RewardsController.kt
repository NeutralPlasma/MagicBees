package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.rewards.Reward
import eu.virtusdevelops.magicbees.api.rewards.RewardParser

interface RewardsController : Controller {

    fun getAllParsers(): Set<RewardParser>

    fun registerRewardParser(parser: RewardParser)

    fun getRewards(data: List<String>): List<Reward>
}