package eu.virtusdevelops.magicbees.gui

import eu.virtusdevelops.magicbees.api.controllers.Controller
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class GuiController(
    private val plugin: JavaPlugin,
) : Controller {

    private val players: MutableSet<UUID> = mutableSetOf()
    private lateinit var listener: GUIListener

    override fun init(): Boolean{
        listener = GUIListener(plugin, this)
        plugin.server.pluginManager.registerEvents(listener, plugin)
        return true
    }

    override fun reload() {
        disable()
        init()
    }


    fun disable(){
        HandlerList.unregisterAll(listener)
    }


    fun addPlayer(player: UUID){
        players.add(player)
    }

    fun removePlayer(player: UUID){
        players.remove(player)
    }

    fun hasPlayer(player: UUID): Boolean{
        return players.contains(player)
    }


}