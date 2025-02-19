package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.api.requirements.AdvancedIntegerRequirement

class ItemRequirementParser : RequirementParser {


    override fun getName(): String {
        return "Item"
    }

    override fun parse(data: String): Requirement {
        val split = data.split(":")
        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider("Item")
            ?: throw IllegalStateException("Item provider is not present!")

        if(provider !is AdvancedProvider<*, *>) throw IllegalArgumentException("Invalid provider!")


        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")
        val itemAmount = split[2].toInt()
        val itemName = split[1]

        return AdvancedIntegerRequirement(itemName, itemAmount, provider as AdvancedProvider<String, Int>)
    }
}