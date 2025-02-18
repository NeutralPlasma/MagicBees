package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemProvider : AdvancedProvider<String, Int> {
    private var items: HashMap<String, ItemStack> = HashMap()
    private var initialized: Boolean = false


    // load items from items.yml configuration file
    //
    override fun init() {


        TODO("Not yet implemented")
    }

    override fun isInitialized(): Boolean {
        return initialized
    }

    override fun getName(): String {
        return "Item"
    }

    override fun give(player: Player, value: String, amount: Int): Boolean {
        val item = items[value]?.clone() ?: return false

        // add player utils for item giving
        // try to give to player if no space return false
        item.amount = amount
        player.inventory.addItem(item)
        return true
    }

    override fun give(player: Player, value: String): Boolean {
        return give(player, value, 1)
    }


    override fun take(player: Player, value: String, amount: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun take(player: Player, value: String): Boolean {
        return take(player, value, 1)
    }


    override fun has(player: Player, value: String, amount: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(player: Player, value: String): Boolean {
        return has(player, value, 1)
    }


    override fun set(player: Player, value: String, amount: Int): Boolean {
        // first check if player already has the item, if he has it then set the amount to the new amount
        // if player doesnt have the item give the set amount
        TODO("Not yet implemented")
    }

    override fun set(player: Player, value: String): Boolean {
        return set(player, value, 1)
    }


    fun getItem(value: String): ItemStack? = items[value]
}