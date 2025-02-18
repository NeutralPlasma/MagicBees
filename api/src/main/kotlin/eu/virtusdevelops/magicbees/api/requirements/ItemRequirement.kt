package eu.virtusdevelops.magicbees.api.requirements

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemRequirement(private val itemName: String, private val itemAmount: Int, private val provider: AdvancedProvider<String, Int>) :
    Requirement {



    override fun check(player: Player): Boolean {
        return provider.has(player, itemName, itemAmount)
    }

    override fun processRequirement(player: Player): Boolean {
        return provider.take(player, itemName, itemAmount)
    }

    override fun getIcon(): ItemStack {


        // TODO
        /*return provider.getItem(itemName) ?: */
        return ItemStack(Material.DIAMOND)
    }
}