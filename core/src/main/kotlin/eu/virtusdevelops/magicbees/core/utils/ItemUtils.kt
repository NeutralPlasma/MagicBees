package eu.virtusdevelops.magicbees.core.utils

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.function.BiPredicate
import java.util.function.Consumer
import kotlin.math.min


object ItemUtils {
    fun encodeItem(itemStack: ItemStack?): String {
        val yamlConfiguration = YamlConfiguration()
        yamlConfiguration["item"] = itemStack
        return Base64.getEncoder().encodeToString(yamlConfiguration.saveToString().toByteArray())
    }

    fun decodeItem(encodedItem: String?): ItemStack? {
        val yamlConfiguration = YamlConfiguration()

        try {
            yamlConfiguration.loadFromString(String(Base64.getDecoder().decode(encodedItem)))
        } catch (e: InvalidConfigurationException) {
            //Logger.getLogger("").warning("Error while decoding item: $e")
            return null
        }

        return yamlConfiguration.getItemStack("item")
    }


    /**
     * Compares the traits of two items
     * @param first The first ItemStack
     * @param second The second ItemStack
     * @param datas The ItemTraits to compare
     * @return Whether the two items are identical in terms of the traits provided. Returns true if both items are null, and false if only one is null.
     */
    fun compare(first: ItemStack?, second: ItemStack?, vararg datas: ItemData): Boolean {
        if (first === second) {
            return true
        }
        if (first == null || second == null) {
            return false
        }
        for (data in datas) {
            if (!data.compare(first, second)) {
                return false
            }
        }
        return true
    }

    /**
     * Compares the type, name, and lore of two items
     * @param first The first ItemStack
     * @param second The second ItemStack
     * @return Whether the two items are identical in terms of type, name, and lore. Returns true if both items are null, and false if only one is null.
     */
    fun compare(first: ItemStack?, second: ItemStack?): Boolean {
        return compare(first, second, ItemData.TYPE, ItemData.NAME, ItemData.LORE)
    }


    /**
     * Renames an ItemStack, functionally identical to [ItemUtils.setName] but kept for legacy reasons
     * @param item The ItemStack to be renamed
     * @param name The name to give the ItemStack
     * @return The renamed ItemStack
     */
    fun rename(item: ItemStack, name: String?): ItemStack {
        val meta = item.itemMeta
        meta.setDisplayName(name)
        val clone = item.clone()
        clone.setItemMeta(meta)
        return clone
    }

    /**
     * Renames an ItemStack
     * @param item The ItemStack to be renamed
     * @param name The name to give the ItemStack
     * @return The renamed ItemStack
     */
    fun setName(item: ItemStack, name: String?): ItemStack {
        return rename(item, name)
    }

    /**
     * Set a single line of lore for an ItemStack
     * @param item The ItemStack to be given lore
     * @param line The line of lore to be given
     * @return The modified ItemStack
     */
    fun setLore(item: ItemStack, line: String): ItemStack {
        val meta = item.itemMeta
        val lore: MutableList<String> = ArrayList()
        lore.add(line)
        meta.lore = lore
        val clone = item.clone()
        clone.setItemMeta(meta)
        return clone
    }

    /**
     * Set multiple lines of lore for an ItemStack
     * @param item The ItemStack to be given lore
     * @param lore The lines of lore to be given
     * @return The modified ItemStack
     */
    fun setLore(item: ItemStack, lore: List<String>): ItemStack {
        val newLore = lore.stream().map {
            MiniMessage.miniMessage().deserialize(it)
        }.toList()

        val meta = item.itemMeta
        meta.lore(newLore)
        item.setItemMeta(meta)
        return item
    }

    /**
     * Add a line of lore to an ItemStack
     * @param item The ItemStack to be given lore
     * @param line The line of lore to add
     * @return The modified ItemStack
     */
    fun addLore(item: ItemStack, line: String): ItemStack {
        val meta = item.itemMeta
        var lore = meta.lore()
        lore = lore ?: ArrayList()
        lore.add(MiniMessage.miniMessage().deserialize(line))
        meta.lore(lore)
        item.setItemMeta(meta)
        return item
    }

    /**
     * Adds multiple lines of lore to an ItemStack
     * @param item The ItemStack to be given lore
     * @param lines The lines or lore to add
     * @return The modified ItemStack
     */
    fun addLore(item: ItemStack, lines: Iterable<String>): ItemStack {
        val meta = item.itemMeta
        var lore = meta.lore()
        lore = lore ?: ArrayList()

        lines.forEach {
            lore.add(MiniMessage.miniMessage().deserialize(it))
        }

        item.setItemMeta(meta)
        return item
    }

    /**
     * Set multiple lines of lore for an ItemStack
     * @param item The ItemStack to be given lore
     * @param lore The lines of lore to be given
     * @return The modified ItemStack
     */
    fun setLore(item: ItemStack, vararg lore: String): ItemStack {
        return setLore(item, listOf(*lore))
    }

    /**
     * Sets an item to be unbreakable
     * @param item The item to make unbreakable
     * @return The unbreakable item
     */
    fun setUnbreakable(item: ItemStack): ItemStack {
        val meta = item.itemMeta
        meta.isUnbreakable = true
        item.setItemMeta(meta)
        return item
    }

    /**
     * Add an enchantment to an ItemStack
     * @param item The ItemStack to be enchanted
     * @param enchant The Enchantment to add to the ItemStack
     * @param level The level of the Enchantment
     * @return The enchanted ItemStack
     */
    fun addEnchant(item: ItemStack, enchant: Enchantment, level: Int): ItemStack {
        val meta = item.itemMeta
        meta.addEnchant(enchant, level, true)
        if (level == 0) {
            meta.removeEnchant(enchant)
        }
        item.setItemMeta(meta)
        return item
    }



    /**
     * Adds ItemFlags to the item
     * @param item The item to add ItemFlags to
     * @param flags The ItemFlags to add
     * @return The modified item
     */
    fun addItemFlags(item: ItemStack, vararg flags: ItemFlag): ItemStack {
        val meta = item.itemMeta
        meta.addItemFlags(*flags)
        item.setItemMeta(meta)
        return item
    }

    /**
     * Sets the custom model data of the item
     * @param item The item to set the custom model data for
     * @param customModelData The custom model data to set
     * @return The modified item
     */
    fun setCustomModelData(item: ItemStack, customModelData: Int): ItemStack {
        val meta = item.itemMeta
        meta.setCustomModelData(customModelData)
        item.setItemMeta(meta)
        return item
    }




    /**
     * Damages an item
     * @param item The item to damage
     * @param amount How much damage to apply
     * @return The damaged item
     * @throws IllegalArgumentException if the item is not damageable
     */
    fun damage(item: ItemStack, amount: Int): ItemStack {

        val meta = item.itemMeta
        require(meta is Damageable) { "Item must be damageable" }
        meta.damage += amount
        item.setItemMeta(meta)
        return item

    }


    /**
     * Counts the number of the given item in the given inventory
     * @param inv The inventory to count the items in
     * @param item The item to count
     * @return The number of items found
     */
    @JvmOverloads
    fun count(
        inv: Inventory,
        item: ItemStack,
        comparison: BiPredicate<ItemStack, ItemStack> = BiPredicate { obj: ItemStack, stack: ItemStack ->
            obj.isSimilar(stack)
        }
    ): Int {
        var count = 0
        for (i in inv) {
            if(i == null) continue
            if (comparison.test(item, i)) {
                count += i.amount
            }
        }
        return count
    }


    fun get(
        inv: Inventory,
        itemStack: ItemStack,
        comparison: BiPredicate<ItemStack, ItemStack> = BiPredicate { obj: ItemStack, stack: ItemStack ->
            obj.isSimilar(stack)
        }

    ): ItemStack? {
        for (i in inv) {
            if(i == null) continue
            if (comparison.test(itemStack, i)) {
                return i
            }
        }
        return null
    }

    fun get(
        player: Player,
        itemStack: ItemStack,
        comparison: BiPredicate<ItemStack, ItemStack> = BiPredicate { obj: ItemStack, stack: ItemStack ->
            obj.isSimilar(stack)
        }
    ): ItemStack? {
        val inv = player.inventory
        val itemInHand = player.inventory.itemInMainHand
        if(comparison.test(itemStack, itemInHand)) return itemInHand
        val itemInOffHand = player.inventory.itemInOffHand
        if(comparison.test(itemStack, itemInOffHand)) return itemInOffHand

        for (i in inv) {
            if(i == null) continue
            if (comparison.test(itemStack, i) && i.amount > 0) {
                return i
            }
        }
        return null
    }

    /**
     * Counts the number of items of the given type in the given inventory
     * @param inv The inventory to count the items in
     * @param type The type of item to count
     * @return The number of items found
     */
    fun count(inv: Inventory, type: Material): Int {
        return count(
            inv, ItemStack(type)
        ) { a: ItemStack?, b: ItemStack? -> compare(a, b, ItemData.TYPE) }
    }

    /**
     * Removes the specified amount of the given item from the given inventory
     * @param inv The inventory to remove the items from
     * @param item The item to be removed
     * @param amount The amount of items to remove
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    @JvmOverloads
    fun remove(
        inv: Inventory,
        item: ItemStack,
        amount: Int,
        comparison: BiPredicate<ItemStack, ItemStack> = BiPredicate { obj: ItemStack, stack: ItemStack ->
            obj.isSimilar(stack)
        }
    ): Boolean {
        var amount = amount
        val contents = inv.contents
        var i = 0
        while (i < contents.size && amount > 0) {
            if (contents[i] == null || !comparison.test(item, contents[i]!!)) {
                i++
                continue
            }
            if (amount >= contents[i]!!.amount) {
                amount -= contents[i]!!.amount
                contents[i] = null
                if (amount == 0) {
                    inv.contents = contents
                    return true
                }
                i++
                continue
            }
            contents[i]!!.amount -= amount
            inv.contents = contents
            return true
        }
        inv.contents = contents
        return false
    }

    /**
     * Removes the specified amount of the given item type from the given inventory
     * @param inv The inventory to remove the items from
     * @param type The item type to be removed
     * @param amount The amount of items to remove
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    fun remove(inv: Inventory, type: Material, amount: Int): Boolean {
        return remove(
            inv, ItemStack(type), amount
        ) { a: ItemStack?, b: ItemStack? -> compare(a, b, ItemData.TYPE) }
    }


    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param item The item to count and remove
     * @param max The maximum number of items to remove
     * @return How many items were removed
     */
    @JvmOverloads
    fun countAndRemove(
        inv: Inventory,
        item: ItemStack,
        max: Int = Int.MAX_VALUE,
        comparison: BiPredicate<ItemStack, ItemStack> = BiPredicate { obj: ItemStack, stack: ItemStack ->
            obj.isSimilar(stack)
        }
    ): Int {
        var count = count(inv, item, comparison)
        count = min(max.toDouble(), count.toDouble()).toInt()
        remove(inv, item, count, comparison)
        return count
    }

    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param type The item type to count and remove
     * @param max The maximum number of items to remove
     * @return How many items were removed
     */
    fun countAndRemove(inv: Inventory, type: Material, max: Int): Int {
        return countAndRemove(
            inv, ItemStack(type), max
        ) { a: ItemStack?, b: ItemStack? -> compare(a, b, ItemData.TYPE) }
    }

    /**
     * Remove all items of a specified type, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param type The item type to count and remove
     * @return How many items were removed
     */
    fun countAndRemove(inv: Inventory, type: Material): Int {
        return countAndRemove(
            inv, ItemStack(type), Int.MAX_VALUE
        ) { a: ItemStack?, b: ItemStack? -> compare(a, b, ItemData.TYPE) }
    }

    /**
     * Give the player the specified items, dropping them on the ground if there is not enough room
     * @param player The player to give the items to
     * @param items The items to be given
     */
    fun give(player: Player, vararg items: ItemStack) {
        player.inventory.addItem(*items).values.forEach(Consumer { i: ItemStack? ->
            player.world.dropItem(
                player.location,
                i!!
            )
        })
    }

    /**
     * Gives the player the specified amount of the specified item, dropping them on the ground if there is not enough room
     * @param player The player to give the items to
     * @param item The item to be given to the player
     * @param amount The amount the player should be given
     */
    fun give(player: Player, item: ItemStack, amount: Int) {
        var amount = amount
        require(amount >= 1) { "Amount must be greater than 0" }
        val stackSize = item.type.maxStackSize
        while (amount > stackSize) {
            val clone = item.clone()
            clone.amount = stackSize
            give(player, clone)
            amount -= stackSize
        }
        val clone = item.clone()
        clone.amount = amount
        give(player, clone)
    }

    /**
     * Gives the player the specified amount of the specified item type, dropping them on the ground if there is not enough room
     * @param player The player to give the items to
     * @param type The item type to be given to the player
     * @param amount The amount the player should be given
     */
    fun give(player: Player, type: Material, amount: Int) {
        give(player, ItemStack(type), amount)
    }

}

