package eu.virtusdevelops.magicbees.gui

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

open class GUI (private val player: Player,
                private val size: Int,
                private val title: String,
                private val noBackgroundSlots: List<Int> = ArrayList()) : InventoryHolder {

    private val activeIcons : MutableMap<Int, Icon?> = mutableMapOf()
    private val allIcons : MutableMap<Int, List<Icon>> = mutableMapOf()

    private val closeActions : MutableList<Action> = mutableListOf()


    init {
        setupBackground()
    }

    fun addIcon(slot: Int, icon: Icon) {
        allIcons[slot] = allIcons.getOrDefault(slot, ArrayList()).plus(icon)
    }

    fun setIcon(slot: Int, icon: Icon){
        allIcons[slot] = listOf(icon)
    }

    fun getIcon(slot: Int): Icon? = activeIcons[slot]


    private fun setupBackground(){
        val stack = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta = stack.itemMeta
        meta.isHideTooltip = true
        stack.itemMeta = meta
        val stack2 = ItemStack(Material.ORANGE_STAINED_GLASS_PANE)
        stack2.itemMeta = meta

        val icon1 = Icon(stack, 0)
        val icon2 = Icon(stack2, 0)


        // set items in inventory
        addIcon(0, icon2)
        addIcon(1, icon2)
        addIcon(7, icon2)
        addIcon(8, icon2)
        addIcon(9, icon2)
        addIcon(17, icon2)


        val start = if (size < 27) {
            -100
        } else if (size < 36) {
            20
        } else if (size < 45) {
            29
        } else if (size < 53) {
            38
        } else {
            47
        }

        for (i in start..<start + 5) {
            if (!noBackgroundSlots.contains(i)) {
                addIcon(i, icon2)
            }
        }

        for (i in 0..<size) {
            if (!noBackgroundSlots.contains(i)) {
                if (getIcon(i) == null) {
                    addIcon(i, icon1)
                }
            }
        }
    }

    private fun setupIcons(){
        for(entry in allIcons){
            val sorted = entry.value.sorted()
            for(icon in sorted){
                if(icon.getVisiblityCondition() != null){
                    if(icon.getVisiblityCondition()!!.check(player, icon)){
                        activeIcons[entry.key] = icon
                        break
                    }
                }else{
                    activeIcons[entry.key] = icon
                    break
                }
            }
        }
    }

    fun refresh(){
        if(player.openInventory.topInventory.holder is GUI){
            setupIcons()
            val inv = player.openInventory.topInventory
            activeIcons.forEach{
                if(it.value != null){
                    it.value!!.refresh(player)
                    inv.setItem(
                        it.key,
                        it.value!!.getItemStack()
                    )
                }
            }
        }
    }
    fun open(){
        player.openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        val inventory = Bukkit.createInventory(this, size, MiniMessage.miniMessage().deserialize(title))
        setupIcons()
        for(entry in activeIcons){
            if(entry.value == null){
                inventory.setItem(entry.key, null)
            }else{
                inventory.setItem(entry.key, entry.value!!.getItemStack())
            }
        }
        return inventory
    }


}