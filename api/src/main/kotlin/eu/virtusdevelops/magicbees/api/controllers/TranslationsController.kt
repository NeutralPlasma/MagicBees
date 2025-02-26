package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.models.Messages
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface TranslationsController : Controller {
    fun sendMessage(recipient: Player, message: Messages, vararg args: String)
    fun getString(message: Messages, vararg args: String): String
    fun getComponent(message: Messages, vararg args: String): Component
    fun getStringList(message: Messages, vararg args: String): List<String>
    fun getComponentList(message: Messages, vararg args: String): List<Component>
    fun getComponentList(message: Messages, vararg args: Component): List<Component>
    fun getString(key: String, vararg args: String): String
}
