package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.requirements.AdvancedDoubleRequirement
import eu.virtusdevelops.magicbees.api.requirements.AdvancedIntegerRequirement
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser

class CoinsEngineRequirementParser : RequirementParser {

    override fun getName(): String {
        return "CoinsEngine"
    }

    override fun parse(data: String): Requirement {
        val split = data.split(":")
        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider("CoinsEngine")
            ?: throw IllegalStateException("Item provider is not present!")

        if(provider !is AdvancedProvider<*, *>) throw IllegalArgumentException("Invalid provider!")


        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")
        val amount = split[2].toDouble()
        val currencyName = split[1]

        return AdvancedDoubleRequirement(currencyName, amount, provider as AdvancedProvider<String, Double>)
    }
}