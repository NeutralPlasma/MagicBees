package eu.virtusdevelops.magicbees.core.requirements.parsers

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.api.requirements.DoubleRequirement

class VaultRequirementParser : RequirementParser {

    override fun getName(): String {
        return "Vault"
    }

    override fun parse(data: String): Requirement {
        val split = data.split(":")
        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider("Vault")
            ?: throw IllegalStateException("Item provider is not present!")

        return DoubleRequirement(split[1].toDouble(), provider as Provider<Double>)
    }
}