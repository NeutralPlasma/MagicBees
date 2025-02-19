package eu.virtusdevelops.magicbees.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin

class GUIListener(
    private val plugin: JavaPlugin,
    private val guiController: GuiController) : Listener {



    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryOpen(event: InventoryOpenEvent){
        if (event.inventory.holder is GUI) {
            guiController.addPlayer(event.player.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClose(event: org.bukkit.event.inventory.InventoryCloseEvent){
        if (event.view.topInventory.holder is GUI) {
            guiController.removePlayer(event.player.uniqueId)
        }else{
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent){
        if(event.clickedInventory == null) return
        val gui = event.clickedInventory!!.holder as? GUI ?: return
        val player = event.whoClicked  as Player? ?: return
        val itemStack = event.currentItem ?: return
        if ( itemStack.type == Material.AIR) return
        val icon = gui.getIcon(event.rawSlot) ?: return


        event.isCancelled = true

//        if (event.cursor != null && event.cursor.type != Material.AIR) {
//            icon.getDragItemActions().forEach { it.execute(player, event.cursor) }
//            return
//        }

        if (event.click == ClickType.LEFT) icon.getLeftClickActions().forEach {
            it.execute(player)
        }
        if (event.click == ClickType.RIGHT) icon.getRightClickActions().forEach {
            it.execute(player)
        }
        if (event.click == ClickType.SHIFT_LEFT) icon.getShiftLeftClickActions().forEach {
            it.execute(player)
        }
        if (event.click == ClickType.SHIFT_RIGHT) icon.getShiftRightClickActions().forEach {
            it.execute(player)
        }

        icon.getClickActions().forEach {  it.execute(player) }
    }
}