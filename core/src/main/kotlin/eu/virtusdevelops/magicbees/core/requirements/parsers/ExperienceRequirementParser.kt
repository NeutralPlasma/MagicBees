package eu.virtusdevelops.magicbees.core.requirements.parsers

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.api.requirements.IntegerRequirement

class ExperienceRequirementParser : RequirementParser {
    override fun getName(): String {
        return "Experience"
    }

    override fun parse(data: String): Requirement {
        val split = data.split(":")
        if(split.size != 2) throw IllegalArgumentException("Invalid data format!")
        val requiredAmount = split[1].toInt()

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider("Experience")
            ?: throw IllegalStateException("VotingPlugin provider is not present!")

        return IntegerRequirement(requiredAmount, provider as Provider<Int>)
    }
}