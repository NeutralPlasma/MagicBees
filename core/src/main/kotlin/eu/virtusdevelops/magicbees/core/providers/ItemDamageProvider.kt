package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class ItemDamageProvider : AdvancedProvider<ItemStack, Int> {

    override fun init() {
        //
    }

    override fun isInitialized(): Boolean {
        return true
    }

    override fun getName(): String {
        return "Durability"
    }

    override fun give(player: Player, value: ItemStack, amount: Int): Boolean {
        val meta = value.itemMeta
        if(meta is Damageable){
            meta.damage += amount
            value.itemMeta = meta
            return true
        }
        return false
    }

    override fun give(player: Player, value: ItemStack): Boolean {
        return give(player, value, 1)
    }

    override fun take(player: Player, value: ItemStack, amount: Int): Boolean {
        val meta = value.itemMeta
        if(meta is Damageable){
            // add checks for unbreaking enchant and unbreakable modifier

            if(meta.damage < amount) return false
            meta.damage -= amount
            value.itemMeta = meta
            return true
        }
        return false
    }

    override fun take(player: Player, value: ItemStack): Boolean {
        return take(player, value, 1)
    }

    override fun has(player: Player, value: ItemStack, amount: Int): Boolean {
        val meta = value.itemMeta
        if(meta is Damageable){
            // add checks for unbreaking enchant and unbreakable modifier

            return meta.damage >= amount
        }
        return false
    }

    override fun has(player: Player, value: ItemStack): Boolean {
        return has(player, value, 1)
    }

    override fun set(player: Player, value: ItemStack, amount: Int): Boolean {
        val meta = value.itemMeta
        if(meta is Damageable){
            meta.damage = amount
            value.itemMeta = meta
            return true
        }
        return false
    }

    override fun set(player: Player, value: ItemStack): Boolean {
        return set(player, value, 1)
    }




}