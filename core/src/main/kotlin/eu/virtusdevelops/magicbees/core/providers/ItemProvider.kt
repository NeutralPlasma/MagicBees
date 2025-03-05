package eu.virtusdevelops.magicbees.core.providers

import com.destroystokyo.paper.profile.PlayerProfile
import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import eu.virtusdevelops.magicbees.core.utils.ItemData
import eu.virtusdevelops.magicbees.core.utils.ItemUtils
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.net.MalformedURLException
import java.net.URI
import java.util.*
import java.util.function.BiPredicate
import java.util.logging.Logger
import kotlin.collections.HashMap

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
        fileStorage.saveData()

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
        val count = ItemUtils.count(player.inventory, getItem(value) ?: return false, matcherAll)
        if(count < amount){
            return false
        }
        ItemUtils.remove(player.inventory, getItem(value) ?: return false, amount, matcherAll)

        return true
    }

    override fun take(player: Player, value: String): Boolean {
        return take(player, value, 1)
    }


    fun has(player: Player, value: String, amount: Int, ignoreEnchants: Boolean, ignoreLore: Boolean): Boolean{
        val matcher = getMatcher(ignoreEnchants, ignoreLore)
        val count = ItemUtils.count(player.inventory, getItem(value) ?: return false, matcher)
        return count >= amount
    }

    override fun has(player: Player, value: String, amount: Int): Boolean {
        return has(player, value, amount, false, false)
    }

    override fun has(player: Player, value: String): Boolean {
        return has(player, value, 1)
    }


    override fun set(player: Player, value: String, amount: Int): Boolean {
        val count = ItemUtils.count(player.inventory, getItem(value) ?: return false, matcherAll)
        if(count < amount){
            ItemUtils.give(player, getItem(value) ?: return false, amount - count)
        }else if(count > amount){
            ItemUtils.remove(player.inventory, getItem(value) ?: return false, count - amount, matcherAll)
        }
        return true
    }

    override fun set(player: Player, value: String): Boolean {
        return set(player, value, 1)
    }


    fun getPlayerItem(player: Player, value: String): ItemStack?{
        return getPlayerItem(player, value, false, false)
    }

    fun getPlayerItem(player: Player, value: String, ignoreEnchants: Boolean, ignoreLore: Boolean): ItemStack?{
        val matcher = getMatcher(ignoreEnchants, ignoreLore)
        ItemUtils.get(player, getItem(value) ?: return null, matcher)?.let {
            return it
        }
        return null
    }


    fun getItem(value: String): ItemStack? {
        if(value.startsWith("!")){
            return ItemStack(Material.matchMaterial(value.substring(1).uppercase()) ?: return null)
        }
        return items[value]?.clone()
    }


    fun getAllItemKeys(): List<String>{
        return items.keys.toList()
    }

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

        // check for head value
        section.getString("head")?.let {
            if(material == Material.PLAYER_HEAD && meta is org.bukkit.inventory.meta.SkullMeta){
                val profile = getPlayerProfile(it, section.getString("uuid")?.let { UUID.fromString(it) })
                meta.playerProfile = profile

                section.set("uuid", profile.id?.toString() ?: "null")
                logger.info("Setting uuid ${profile.id} for player head.")
            }
        }
        section.getBoolean("glow").let {
            if(it){
                meta.addEnchant(Enchantment.UNBREAKING, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
        }

        item.itemMeta = meta
        return item
    }


    private fun getPlayerProfile(headTexture: String, uuid: UUID? = null): PlayerProfile {
        var newUUID = uuid
        if(newUUID == null)
            newUUID = UUID.randomUUID()

        val profile = Bukkit.createProfile(newUUID!!)
        val textures = profile.textures
        try{
            textures.skin = URI.create("https://textures.minecraft.net/texture/$headTexture").toURL()
            profile.setTextures(textures)
            return profile
        }catch (exception: MalformedURLException){
            exception.printStackTrace()
            return profile
        }
    }

    private fun getMatcher(ignoreEnchants: Boolean, ignoreLore: Boolean): BiPredicate<ItemStack, ItemStack> {
        Bukkit.getConsoleSender().sendMessage("Info: $ignoreEnchants, $ignoreLore")
        if(!ignoreEnchants && !ignoreLore)
             return matcherAll
        if(ignoreEnchants && !ignoreLore)
            return matcherLore

        Bukkit.getConsoleSender().sendMessage("Lore and enchants are ignored.")

        return matcherAny
    }

    private val matcherAny = object: BiPredicate<ItemStack, ItemStack> {
        override fun test(t: ItemStack, u: ItemStack): Boolean {
            return ItemData.TYPE.compare(t, u)
                    //&& ItemData.NAME.compare(t, u)
        }
    }

    private val matcherLore = object: BiPredicate<ItemStack, ItemStack> {
        override fun test(t: ItemStack, u: ItemStack): Boolean {
            return ItemData.TYPE.compare(t, u)
                    && ItemData.NAME.compare(t, u)
                    && ItemData.LORE.compare(t, u)
        }
    }


    private val matcherAll = object: BiPredicate<ItemStack, ItemStack> {
        override fun test(t: ItemStack, u: ItemStack): Boolean {
            return ItemData.TYPE.compare(t, u)
                    && ItemData.NAME.compare(t, u)
                    && ItemData.LORE.compare(t, u)
                    && ItemData.ENCHANTMENTS.compare(t, u)
        }
    }
}