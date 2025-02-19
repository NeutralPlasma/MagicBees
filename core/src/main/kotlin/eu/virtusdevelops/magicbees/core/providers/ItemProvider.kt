package eu.virtusdevelops.magicbees.core.providers

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import eu.virtusdevelops.magicbees.core.utils.ItemData
import eu.virtusdevelops.magicbees.core.utils.ItemUtils
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.logging.Logger

class ItemProvider(private val fileStorage: FileStorage, private val logger: Logger) : AdvancedProvider<String, Int> {
    private var items: HashMap<String, ItemStack> = HashMap()
    private var initialized: Boolean = false


    // load items from items.yml configuration file
    //
    override fun init() {

        items.clear()
        fileStorage.loadData()
        val config = fileStorage.getConfiguration()!!
        config.getKeys(false).forEach { key ->
            // check if base64
            if(config.getString("$key.base64") != null){
                 try{
                     ItemUtils.decodeItem(config.getString("$key.base64")!!)?.let {
                         items[key] = it
                     }
                 }catch (e: Exception){
                     logger.severe("Error while loading item $key from ${fileStorage.filePath} file.")
                 }
            }else{
                // probably default not base64
                val item = getItem(config.getConfigurationSection(key)!!)
                if(item != null)
                    items[key] = item

            }
        }

        initialized = true
    }

    override fun isInitialized(): Boolean {
        return initialized
    }

    override fun getName(): String {
        return "Item"
    }

    override fun give(player: Player, value: String, amount: Int): Boolean {
        val item = getItem(value) ?: return false
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
        }
        ItemUtils.remove(player.inventory, getItem(value) ?: return false, amount)

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


    fun getItem(value: String): ItemStack? = items[value]?.clone()


    private fun getItem(section: ConfigurationSection): ItemStack?{

        val material = section.getString("material")?.let { Material.matchMaterial(it) } ?: return null

        val item = ItemStack(material)


        val meta = item.itemMeta ?: return item
        val rawString = section.getString("name")
        if(rawString != null)
            meta.displayName(
                MiniMessage.miniMessage().deserialize(rawString)
                    .decorations(setOf(TextDecoration.ITALIC), false)
            )


        if(section.isList("lore")) {
            val loreList = section.getStringList("lore")
            meta.lore(loreList.map {
                MiniMessage.miniMessage().deserialize(it)
                    .decorations(setOf(TextDecoration.ITALIC), false)
            })
        }

        item.itemMeta = meta
        return item
    }
}