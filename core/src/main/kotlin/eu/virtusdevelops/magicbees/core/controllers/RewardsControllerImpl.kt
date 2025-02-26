package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.RewardsController
import eu.virtusdevelops.magicbees.api.rewards.Reward
import eu.virtusdevelops.magicbees.api.rewards.RewardParser
import eu.virtusdevelops.magicbees.core.rewards.*
import java.util.logging.Logger

class RewardsControllerImpl(
    private val logger: Logger
) : RewardsController {

    private val rewardParsers: MutableMap<String, RewardParser> = mutableMapOf()


    override fun init(): Boolean {
        logger.info("Initializing rewards controller...")
        registerRewardParser(ItemRewardParser())
        registerRewardParser(ExperienceRewardParser())
        registerRewardParser(VaultRewardParser())
        registerRewardParser(VotingPluginRewardParser())
        registerRewardParser(CoinsEngineRewardParser())
        return true
    }

    override fun reload() {
    }

    override fun registerRewardParser(parser: RewardParser) {
        rewardParsers[parser.getName()] = parser
    }

    override fun getRewards(data: List<String>): List<Reward> {

        val rewards: MutableList<Reward> = mutableListOf()
        for(line in data) {
            rewards.add(rewardParsers[line.substringBefore(":")]?.parseReward(line) ?: continue)
        }
        return rewards
    }

    override fun getAllParsers(): Set<RewardParser> = rewardParsers.values.toSet()
}