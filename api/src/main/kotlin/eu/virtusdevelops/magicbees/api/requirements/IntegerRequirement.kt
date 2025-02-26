package eu.virtusdevelops.magicbees.api.requirements

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.Provider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class IntegerRequirement (private val amount: Int,
                          private val provider: Provider<Int>): Requirement {



    override fun check(player: Player): Boolean {
        return provider.has(player, amount)
    }

    override fun processRequirement(player: Player): Boolean {
        return provider.take(player, amount)
    }

    override fun getIcon(): ItemStack {
        // setup some icon add language processor to parse translations


        return ItemStack(Material.EXPERIENCE_BOTTLE)
    }

    override fun toString(): String {
        return "${provider.getName()}:$amount"
    }

    override fun getName(): String = "$amount"

    override fun getType(): String =
        MagicBeesAPI.get()?.getTranslationsController()?.getString(provider.getName().uppercase()) ?: provider.getName()
}