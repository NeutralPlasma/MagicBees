package eu.virtusdevelops.magicbees.api.requirements

import eu.virtusdevelops.magicbees.api.Provider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class DoubleRequirement (private val amount: Double,
                         private val provider: Provider<Double>): Requirement {




    override fun check(player: Player): Boolean {
        return provider.has(player, amount)
    }

    override fun processRequirement(player: Player): Boolean {
        return provider.take(player, amount)
    }

    override fun getIcon(): ItemStack {
        // setup some icon add language processor to parse translations


        return ItemStack(Material.SUNFLOWER)
    }

    override fun toString(): String {
        return "${provider.getName()}:$amount"
    }

    override fun getName(): String = "$amount"

    override fun getType(): String = provider.getName()
}