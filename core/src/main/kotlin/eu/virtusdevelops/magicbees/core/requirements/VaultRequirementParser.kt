package eu.virtusdevelops.magicbees.core.requirements

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
        if(split.size != 2) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider("Vault")
            ?: throw IllegalStateException("Vault provider is not present!")

        return DoubleRequirement(split[1].toDouble(), provider as Provider<Double>)
    }

}