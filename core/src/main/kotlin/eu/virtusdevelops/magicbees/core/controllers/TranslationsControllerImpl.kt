package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.MessageContent
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import java.text.MessageFormat
import java.util.logging.Logger

class TranslationsControllerImpl(
    private val logger: Logger,
    private val fileStorage: FileStorage
) : TranslationsController {


    private lateinit var translations: MutableMap<String, MessageContent>

    override fun init(): Boolean {
        translations = mutableMapOf()
        logger.info("Initializing translations controller...")
        reload()
        return true
    }

    override fun reload() {
        translations.clear()

        fileStorage.loadData()
        val config = fileStorage.getConfiguration() ?: return
        config.getKeys(false).forEach {
            // get as list or string

            if(config.isList(it)){
                translations[it] = MessageContent.Multiple(config.getStringList(it))
            }else{
                translations[it] = MessageContent.Single(config.getString(it) ?: "")
            }
        }
    }

    override fun sendMessage(recipient: Player, message: Messages, vararg args: String) {
        // Detect whether the message is single-line or multiline, and send accordingly
        val content = getMessage(message)
        when (content) {
            is MessageContent.Single -> {
                recipient.sendMessage(formatComponent(content.text, *args))
            }
            is MessageContent.Multiple -> {
                content.lines.forEach { line ->
                    recipient.sendMessage(formatComponent(line, *args))
                }
            }
        }
    }

    override fun getComponent(message: Messages, vararg args: String): Component {
        val content = getMessage(message)
        return when (content) {
            is MessageContent.Single -> formatComponent(content.text, *args)
            is MessageContent.Multiple -> formatComponent(content.lines.joinToString("\n"), *args)
        }.decorate()
    }

    override fun getString(message: Messages, vararg args: String): String {
        val content = getMessage(message)
        return when (content) {
            is MessageContent.Single -> formatString(content.text, *args)
            is MessageContent.Multiple -> content.lines.joinToString("\n") { formatString(it, *args) }
        }
    }

    override fun getString(key: String, vararg args: String): String {
        val content = getMessage(key) ?: return key
        return when (content) {
            is MessageContent.Single -> formatString(content.text, *args)
            is MessageContent.Multiple -> content.lines.joinToString("\n") { formatString(it, *args) }
        }
    }

    override fun getStringList(message: Messages, vararg args: String): List<String> {
        val content = getMessage(message)
        return when (content) {
            is MessageContent.Single -> listOf(formatString(content.text, *args))
            is MessageContent.Multiple -> content.lines.map { formatString(it, *args) }
        }
    }

    override fun getComponentList(message: Messages, vararg args: String): List<Component> {
        val content = getMessage(message)
        return when (content) {
            is MessageContent.Single -> listOf(formatComponent(content.text, *args))
            is MessageContent.Multiple -> content.lines.map { formatComponent(it, *args) }
        }.map { it.decorate() }
    }

    override fun getComponentList(message: Messages, vararg args: Component): List<Component> {
        val content = getMessage(message)
        return when (content) {
            is MessageContent.Single -> listOf(format(content.text, *args))
            is MessageContent.Multiple -> content.lines.map { format(it, *args) }
        }.map { it.decorate() }
    }

    private fun getMessage(key: String): MessageContent? {
        val content: MessageContent = translations[key]
            ?: try{
                return Messages.valueOf(key).defaultMessage
            }catch (_: IllegalArgumentException){
                return null
            }
        return content
    }

    private fun getMessage(message: Messages): MessageContent {
        val content: MessageContent = translations[message.name]
            ?: message.defaultMessage
        return content
    }

    // Helper function to format a string with placeholders
    private fun formatString(template: String, vararg args: String): String {
        return MessageFormat.format(template, *args)
    }

    // Helper function to create a MiniMessage component with placeholders
    private fun formatComponent(template: String, vararg args: String): Component {
        val formattedString = formatString(template, *args)
        return MiniMessage.miniMessage().deserialize(formattedString)
            .decoration(TextDecoration.ITALIC, false)
    }

    /**
     * Formats a `Component` template by replacing placeholders such as {0}, {1}, etc. with provided `Component` arguments.
     *
     * @param template The string template containing placeholders (in MiniMessage format if needed).
     * @param args The arguments to replace placeholders. Can include `Component`s.
     * @return A formatted `Component`.
     */
    private fun format(template: String, vararg args: Component): Component {
        var result =  MiniMessage.miniMessage().deserialize(template)
        args.forEachIndexed { index, arg ->
            // Replace placeholders like {0}, {1}, etc., with serialized Components
            result = result.replaceText {
                it.matchLiteral("{$index}")
                    .replacement(arg)
            }
        }
        // Parse the final template with MiniMessage
        return result.decoration(TextDecoration.ITALIC, false)
    }


}