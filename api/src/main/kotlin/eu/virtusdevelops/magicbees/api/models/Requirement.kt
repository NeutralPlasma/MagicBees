package eu.virtusdevelops.magicbees.api.models

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Requirement{
    /**
     * Determines whether the specified player meets a certain requirement.
     * Useful to predetermine if all requirements in set are valid then execute them
     *
     * @param player The player whose eligibility needs to be checked.
     * @return True if the player meets the requirement, otherwise false.
     */
    fun check(player: Player): Boolean

    /**
     * This method also invokes the things that requirement does after wards (like removing items, money and so on)
     *
     * @param player The player whose requirement is being processed.
     * @return True if the player fulfills the requirement, otherwise false.
     */
    fun processRequirement(player: Player): Boolean


    /**
     * Retrieves the icon associated with the requirement.
     *
     * @return The ItemStack representing the icon.
     */
    fun getIcon(): ItemStack
}
