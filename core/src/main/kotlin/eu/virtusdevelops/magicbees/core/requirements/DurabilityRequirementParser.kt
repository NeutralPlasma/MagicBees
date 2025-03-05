package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.requirements.AdvancedDoubleRequirement
import eu.virtusdevelops.magicbees.api.requirements.AdvancedIntegerRequirement
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.core.providers.ItemProvider
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

class DurabilityRequirementParser : RequirementParser {

    override fun getName(): String {
        return "Durability"
    }

    override fun parse(data: String): Requirement {
        val split = data.split(":")
        if(split.size < 3) throw IllegalArgumentException("Invalid data format!")

        val provider = MagicBeesAPI.get()?.getProvidersController()?.getProvider("Durability")
            ?: throw IllegalStateException("Durability provider is not present!")

        val itemProvider = MagicBeesAPI.get()?.getProvidersController()?.getProvider("Item")
            ?: throw IllegalStateException("Item provider is not present!")

        if(provider !is AdvancedProvider<*, *>) throw IllegalArgumentException("Invalid provider!")
        if(itemProvider !is ItemProvider) throw IllegalArgumentException("Invalid provider!")

        val amount = split[2].toInt()
        val itemName = split[1]

        val ignoreEnchants = if (split.size >= 4) split[3].toBoolean() else false
        val ignoreLore = if (split.size >= 5) split[4].toBoolean() else false


        Bukkit.getConsoleSender().sendMessage("Information: $itemName, $amount, $ignoreEnchants, $ignoreLore")

        return AdvancedItemRequirement(itemName, amount, provider as AdvancedProvider<ItemStack, Int>, itemProvider, ignoreEnchants, ignoreLore)
    }
}