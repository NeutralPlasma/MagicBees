package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.core.utils.ItemData
import eu.virtusdevelops.magicbees.core.utils.ItemUtils
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
        item.amount = amount
        ItemUtils.give(player, item)
        return true
    }

    override fun give(player: Player, value: String): Boolean {
        return give(player, value, 1)
    }


    override fun take(player: Player, value: String, amount: Int): Boolean {
        val count = ItemUtils.count(player.inventory, getItem(value) ?: return false)
        if(count < amount){
            return false
        }else if(count > amount){
            ItemUtils.remove(player.inventory, getItem(value) ?: return false, count - amount)
        }
        return true
    }

    override fun take(player: Player, value: String): Boolean {
        return take(player, value, 1)
    }


    override fun has(player: Player, value: String, amount: Int): Boolean {
        return ItemUtils.count(player.inventory, getItem(value) ?: return false) >= amount
    }

    override fun has(player: Player, value: String): Boolean {
        return has(player, value, 1)
    }


    override fun set(player: Player, value: String, amount: Int): Boolean {
        val count = ItemUtils.count(player.inventory, getItem(value) ?: return false)
        if(count < amount){
            ItemUtils.give(player, getItem(value) ?: return false, amount - count)
        }else if(count > amount){
            ItemUtils.remove(player.inventory, getItem(value) ?: return false, count - amount)
        }
        return true
    }

    override fun set(player: Player, value: String): Boolean {
        return set(player, value, 1)
    }


    fun getItem(value: String): ItemStack? = items[value]
}