package eu.virtusdevelops.magicbees.core.utils

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.function.BiPredicate

enum class ItemData(val compare: BiPredicate<ItemStack, ItemStack>) {
    /**
     * For comparing the durability of two items
     */
    DURABILITY(BiPredicate { a: ItemStack, b: ItemStack ->
        val meta1 = a.itemMeta
        val meta2 = b.itemMeta
        if(meta1 is Damageable && meta2 is Damageable) {
            return@BiPredicate meta1.damage == meta2.damage
        }
        return@BiPredicate (
            meta1 != null && meta2 != null && meta1.isUnbreakable == meta2.isUnbreakable
        )
    }),

    /**
     * For comparing the amount of two items
     */
    AMOUNT(BiPredicate { a: ItemStack, b: ItemStack -> a.amount == b.amount }),

    /**
     * For comparing the display name of two items
     */
    NAME(BiPredicate { a: ItemStack, b: ItemStack ->
        if (!a.hasItemMeta() && !b.hasItemMeta()) {
            return@BiPredicate true
        }
        if (!a.hasItemMeta()) {
            return@BiPredicate true
        }
        if (a.itemMeta.hasDisplayName() != b.itemMeta.hasDisplayName()) {
            return@BiPredicate false
        }
        if (!a.itemMeta.hasDisplayName()) {
            return@BiPredicate true
        }
        a.itemMeta.displayName()!!.equals(b.itemMeta.displayName())
    }),

    /**
     * For comparing the lore of two items
     */
    LORE(BiPredicate { a: ItemStack, b: ItemStack ->
        if (!a.hasItemMeta() && !b.hasItemMeta()) {
            return@BiPredicate true
        }

        if (a.itemMeta.hasLore() != b.itemMeta.hasLore()) {
            return@BiPredicate false
        }

        if (!a.itemMeta.hasLore()) {
            return@BiPredicate true
        }

        val lore1 = a.itemMeta.lore()
        val lore2 = b.itemMeta.lore()

        if (lore1!!.size != lore2!!.size) {
            return@BiPredicate false
        }
        for (i in lore1.indices) {
            if (!lore1[i].equals(lore2[i])) {
                return@BiPredicate false
            }
        }
        true
    }),

    /**
     * For comparing the enchantments of two items
     */
    ENCHANTMENTS(BiPredicate { a: ItemStack, b: ItemStack ->
        val enchants1 = a.enchantments
        val enchants2 = b.enchantments
        if (enchants1.size != enchants2.size || !enchants1.keys.containsAll(enchants2.keys)) {
            return@BiPredicate false
        }
        for (ench in enchants1.keys) {
            if (!enchants1[ench]!!.equals(enchants2[ench])) {
                return@BiPredicate false
            }
        }
        true
    }),

    /**
     * For comparing the types of two items
     */
    TYPE(BiPredicate { a: ItemStack, b: ItemStack ->
        if(a.type != b.type) {
            return@BiPredicate false
        }
        a.type == b.type
    });

    /**
     * Compares this trait on the two items
     * @param a The first item
     * @param b The second item
     * @return True if the trait is the same on both items, false otherwise
     */
    fun compare(a: ItemStack, b: ItemStack): Boolean {
        return compare.test(a, b)
    }
}
