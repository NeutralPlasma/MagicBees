package eu.virtusdevelops.magicbees.api.models

enum class Messages(val defaultMessage: MessageContent) {

    // Single-line messages
    HIVE_EMPTY(MessageContent.Single("<red>Beehive is empty")),
    MISSING_REQUIREMENT(MessageContent.Single("<red>You''re missing <gold>{0}<red> {1}")),
    SUCCESSFUL_HARVEST(MessageContent.Single("<green>You successfully harvested")),
    INVALID_PLAYER(MessageContent.Single("<red>Invalid player")),

    // GUI
    BEE_HIVE_MENU_TITLE(MessageContent.Single("<gold>Magic Beehive")),
    BEE_HIVE_STATUS_ICON_TITLE(MessageContent.Single("<gold>Magic Beehive")),
    BEE_HIVE_STATUS_ICON_LORE(
        MessageContent.Multiple(
            listOf(
                "<gray>Honey upgrade level: <gold>{0}",
                "<gray>Comb upgrade level: <gold>{1}",
                "<gray>Bees: <gold>{2}",
                "<gray>Fullness: <gold>{3}"
            )
        )
    ),
    BEE_HIVE_HONEY_UPGRADE_MENU_ICON(MessageContent.Single("<gold>Upgrade honey level")),
    BEE_HIVE_HONEY_LEVEL_UPGRADE_FAIL_LORE(MessageContent.Multiple(listOf(
        "<red>Missing requirement:",
        "{0}"
    ))),
    BEE_HIVE_HONEY_UPGRADE_FAIL_REQUIREMENTS_TEMPLATE(MessageContent.Single("<gray>- <red>{0}"));



    // Utility methods for easy handling
    fun isMultiline(): Boolean {
        return defaultMessage is MessageContent.Multiple
    }

    fun asSingleLine(): String {
        if (defaultMessage is MessageContent.Single) {
            return (defaultMessage as MessageContent.Single).text
        }
        throw IllegalStateException("This message is not a single-line message.")
    }

    fun asLines(): List<String> {
        if (defaultMessage is MessageContent.Multiple) {
            return (defaultMessage as MessageContent.Multiple).lines
        }
        throw IllegalStateException("This message is not a multiline message.")
    }

    // Optionally allow runtime overrides from other sources like YAML
    companion object {
        private val overrides = mutableMapOf<Messages, MessageContent>()

        fun overrideMessage(message: Messages, content: MessageContent) {
            overrides[message] = content
        }

        fun getMessage(message: Messages): MessageContent {
            return overrides[message] ?: message.defaultMessage
        }
    }
}

// Sealed class for message content (typed constraint)
sealed class MessageContent {
    data class Single(val text: String) : MessageContent()
    data class Multiple(val lines: List<String>) : MessageContent()
}