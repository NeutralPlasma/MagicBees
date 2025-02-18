package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.models.Requirement
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemRequirement(data: String, private val provider: AdvancedProvider<String, Int>) : Requirement {

    private val itemName: String
    private val itemAmount: Int


    init {
        val split = data.split(":")
        if(split.size != 3) throw IllegalArgumentException("Invalid data format!")
        itemAmount = split[2].toInt()
        itemName = split[1]
    }


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