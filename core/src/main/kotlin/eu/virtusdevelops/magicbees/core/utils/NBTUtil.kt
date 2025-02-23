package eu.virtusdevelops.magicbees.core.utils

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class NBTUtil {

    companion object {
        lateinit var HONEY_LEVEL_KEY: NamespacedKey
        lateinit var COMB_LEVEL_KEY: NamespacedKey


        fun load(plugin: JavaPlugin){

            HONEY_LEVEL_KEY = NamespacedKey(plugin, "honey_level")
            COMB_LEVEL_KEY = NamespacedKey(plugin, "comb_level")

        }




        fun <T: Any, C: Any> getNBTTag(item: ItemStack, key: NamespacedKey, type: PersistentDataType<T, C>): C? {
            val meta: ItemMeta = item.itemMeta ?: return null // Safely get the ItemMeta, or return null
            val container = meta.persistentDataContainer

            // Explicitly check with type for the key in the container
            if (container.has(key, type)) {
                return container.get(key, type)
            }

            return null // Return null if key is not present
        }


        fun <T, C : Any>setNBTTag(item: ItemStack, key: NamespacedKey, type: PersistentDataType<T, C>, value: C){
            val meta: ItemMeta = item.itemMeta!!
            val container = meta.persistentDataContainer
            if(!container.has(key)){
                container.set(key, type, value)

            }
            item.itemMeta = meta
        }
    }
}