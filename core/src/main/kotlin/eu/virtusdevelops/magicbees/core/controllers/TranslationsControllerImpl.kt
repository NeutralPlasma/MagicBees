package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.Messages
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.text.MessageFormat

class TranslationsControllerImpl : TranslationsController {
    override fun load() {
        TODO("Not yet implemented")
    }

    override fun sendMessage(recipient: Player, message: Messages, vararg args: String) {
        recipient.sendMessage(getString(message, *args))
    }

    override fun getString(message: Messages, vararg args: String): Component {
        return MiniMessage.miniMessage().deserialize(
            MessageFormat.format(message.defaultMessage, *args)
        )
    }
}