package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.MessageContent
import eu.virtusdevelops.magicbees.api.models.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.text.MessageFormat

class TranslationsControllerImpl : TranslationsController {
    override fun load() {
        // Logic to load translations dynamically, if required (e.g., from YAML or database).
        // Can leverage the `Messages.overrideMessage()` method if needed.
        TODO("Not yet implemented")
    }

    override fun sendMessage(recipient: Player, message: Messages, vararg args: String) {
        // Detect whether the message is single-line or multiline, and send accordingly
        val content = Messages.getMessage(message)
        when (content) {
            is MessageContent.Single -> {
                recipient.sendMessage(formatString(content.text, *args))
            }
            is MessageContent.Multiple -> {
                content.lines.forEach { line ->
                    recipient.sendMessage(formatString(line, *args))
                }
            }
        }
    }

    override fun getComponent(message: Messages, vararg args: String): Component {
        val content = Messages.getMessage(message)
        return when (content) {
            is MessageContent.Single -> formatComponent(content.text, *args)
            is MessageContent.Multiple -> formatComponent(content.lines.joinToString("\n"), *args)
        }.decorate()
    }

    override fun getString(message: Messages, vararg args: String): String {
        val content = Messages.getMessage(message)
        return when (content) {
            is MessageContent.Single -> formatString(content.text, *args)
            is MessageContent.Multiple -> content.lines.joinToString("\n") { formatString(it, *args) }
        }
    }

    override fun getStringList(message: Messages, vararg args: String): List<String> {
        val content = Messages.getMessage(message)
        return when (content) {
            is MessageContent.Single -> listOf(formatString(content.text, *args))
            is MessageContent.Multiple -> content.lines.map { formatString(it, *args) }
        }
    }

    override fun getComponentList(message: Messages, vararg args: String): List<Component> {
        val content = Messages.getMessage(message)
        return when (content) {
            is MessageContent.Single -> listOf(formatComponent(content.text, *args))
            is MessageContent.Multiple -> content.lines.map { formatComponent(it, *args) }
        }.map { it.decorate() }
    }

    // Helper function to format a string with placeholders
    private fun formatString(template: String, vararg args: String): String {
        return MessageFormat.format(template, *args)
    }

    // Helper function to create a MiniMessage component with placeholders
    private fun formatComponent(template: String, vararg args: String): Component {
        val formattedString = formatString(template, *args)
        return MiniMessage.miniMessage().deserialize(formattedString)
    }

    // Extension function to add the default text decorations
    private fun Component.decorate(): Component {
        return this.decorations(setOf(TextDecoration.ITALIC), false)
    }
}