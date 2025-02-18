package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.api.models.Requirement
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class IntegerRequirement (data: String, private val provider: Provider<Int>): Requirement {
    private var requiredExperience: Int = 0

    init {
        val split = data.split(":")
        if(split.size != 2) throw IllegalArgumentException("Invalid data format!")
        requiredExperience = split[1].toInt()
    }

    override fun check(player: Player): Boolean {
        return provider.has(player, requiredExperience)
    }

    override fun processRequirement(player: Player): Boolean {
        return provider.take(player, requiredExperience)
    }

    override fun getIcon(): ItemStack {
        // setup some icon add language processor to parse translations


        return ItemStack(Material.EXPERIENCE_BOTTLE)
    }

    override fun toString(): String {
        return "${provider.getName()}:$requiredExperience"
    }


}