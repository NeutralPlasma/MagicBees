package eu.virtusdevelops.magicbees.api.requirements

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AdvancedDoubleRequirement(
    private val key: String,
    private val amount: Double,
    private val provider: AdvancedProvider<String, Double>) : Requirement {



    override fun check(player: Player): Boolean {
        return provider.has(player, key, amount)
    }

    override fun processRequirement(player: Player): Boolean {
        return provider.take(player, key, amount)
    }

    override fun getIcon(): ItemStack {


        // TODO
        /*return provider.getItem(itemName) ?: */
        return ItemStack(Material.DIAMOND)
    }

    override fun getName(): String = "$amount"

    override fun getType(): String = key
}