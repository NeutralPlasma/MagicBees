package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.api.models.Requirement
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class DoubleRequirement (data: String, private val provider: Provider<Double>): Requirement {

    private var requiredMoney: Double = 0.0

    init {
        val split = data.split(":")
        if(split.size != 2) throw IllegalArgumentException("Invalid data format!")
        requiredMoney = split[1].toDouble()
    }

    override fun check(player: Player): Boolean {
        return provider.has(player, requiredMoney)
    }

    override fun processRequirement(player: Player): Boolean {
        return provider.take(player, requiredMoney)
    }

    override fun getIcon(): ItemStack {
        // setup some icon add language processor to parse translations


        return ItemStack(Material.SUNFLOWER)
    }

    override fun toString(): String {
        return "${provider.getName()}:$requiredMoney"
    }

}