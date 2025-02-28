package eu.virtusdevelops.magicbees.api.models

enum class Messages(val defaultMessage: MessageContent) {

    // Single-line messages
    HIVE_EMPTY(MessageContent.Single("<red>Beehive is empty")),
    MISSING_REQUIREMENT(MessageContent.Single("<red>You''re missing <gold>{0}<red> {1}")),
    PASSED_REQUIREMENT(MessageContent.Single("<green>You have <gold>{0} <gold>{1}<green>")),
    SUCCESSFUL_HARVEST(MessageContent.Single("<green>You successfully harvested")),
    INVALID_PLAYER(MessageContent.Single("<red>Invalid player")),
    INVALID_ITEM(MessageContent.Single("<red>Invalid item <yellow>{0}")),
    INVALID_HONEY_LEVEL(MessageContent.Single("<red>Invalid honey level <yellow>{0}")),
    INVALID_COMB_LEVEL(MessageContent.Single("<red>Invalid comb level <yellow>{0}")),
    BEE_HIVE_RECEIVED(MessageContent.Single("<green>You received a magic beehive")),
    BEE_HIVE_GIVEN(MessageContent.Single("<green>Gave a magic beehive to <yellow>{0}")),
    ITEM_RECEIVED(MessageContent.Single("<green>You received <gray>(<yellow>{0}<gray>)")),
    ITEM_GIVEN(MessageContent.Single("<green>Gave <yellow>{0}<green> to <yellow>{1}")),
    NO_PERMISSION(MessageContent.Single("<red>You don't have permission to do that. <gray>(<yellow>{0}<gray>)")),
    BEE_HIVE_PLACED(MessageContent.Single("<green>Magic beehive placed")),
    BEE_HIVE_CONVERTED(MessageContent.Single("<green>Magic beehive converted")),
    BEE_HIVE_BROKEN(MessageContent.Single("<green>Magic beehive broken")),
    BEE_HIVE_MAX_LEVEL(MessageContent.Single("<red>You have reached the maximum level of this hive")),
    SUCCESSFULLY_RELOADED(MessageContent.Single("<green>Successfully reloaded the configuration files <gray>(<yellow>{0}ms<gray>)")),

    // GUI
    BEE_HIVE_MENU_TITLE(MessageContent.Single("<gold>Magic Beehive")),
    BEE_HIVE_STATUS_ICON_TITLE(MessageContent.Single("<gold>Magic Beehive")),
    BEE_HIVE_STATUS_ICON_LORE(
        MessageContent.Multiple(
            listOf(
                "<gray>Honey upgrade level: <gold>{0}",
                "",
                "{4}",
                "",
                "<gray>Comb upgrade level: <gold>{1}",
                "",
                "{5}",
                "",
                "<gray>Bees: <gold>{2}",
                "<gray>Fullness: <gold>{3}"
            )
        )
    ),
    BEE_HIVE_REWARD_TEMPLATE_SAME(MessageContent.Single("<gray>- <green>{0} <gray>(<yellow>{1}<gray>)")),
    BEE_HIVE_REWARD_TEMPLATE(MessageContent.Single("<gray>- <green>{0} <gray>(<yellow>{1}<gray>, <yellow>{2}<gray>)")),
    BEE_HIVE_HONEY_UPGRADE_MENU_ICON(MessageContent.Single("<gold>Upgrade honey level")),
    BEE_HIVE_HONEY_LEVEL_UPGRADE_FAIL_LORE(MessageContent.Multiple(listOf(
        "<gradient:light_purple:gold>Missing requirement:</gradient>",
        "{0}",
        "<gradient:light_purple:gold>Passed requirements:</gradient>",
        "{1}"
    ))),
    BEE_HIVE_HONEY_UPGRADE_FAIL_REQUIREMENTS_TEMPLATE(MessageContent.Single("<gray>- <red>{0}")),
    BEE_HIVE_HONEY_LEVEL_UPGRADE_LORE(MessageContent.Multiple(listOf(
        "<gradient:light_purple:gold>Requirements:</gradient>",
        "{0}"
    ))),
    BEE_HIVE_HONEY_UPGRADE_REQUIREMENTS_TEMPLATE(MessageContent.Single("<gray>- <green>{0}")),
    BEE_HIVE_HONEY_MAXED_LORE(MessageContent.Multiple(listOf(
        "<red>You have reached the maximum honey level"
    ))),



    BEE_HIVE_COMB_UPGRADE_MENU_ICON(MessageContent.Single("<gold>Upgrade comb level")),
    BEE_HIVE_COMB_LEVEL_UPGRADE_FAIL_LORE(MessageContent.Multiple(listOf(
    "<gradient:light_purple:gold>Missing requirement:</gradient>",
    "{0}",
    "<gradient:light_purple:gold>Passed requirements:</gradient>",
    "{1}"
    ))),
    BEE_HIVE_COMB_UPGRADE_FAIL_REQUIREMENTS_TEMPLATE(MessageContent.Single("<gray>- <red>{0}")),
    BEE_HIVE_COMB_LEVEL_UPGRADE_LORE(MessageContent.Multiple(listOf(
    "<gradient:light_purple:gold>Requirements:</gradient>",
    "{0}"
    ))),
    BEE_HIVE_COMB_UPGRADE_REQUIREMENTS_TEMPLATE(MessageContent.Single("<gray>- <green>{0}")),
    BEE_HIVE_COMB_MAXED_LORE(MessageContent.Multiple(listOf(
        "<red>You have reached the maximum comb level"
    ))),


    BEE_HIVE_HONEY_HARVEST_MENU_ICON(MessageContent.Single("<gold>Harvest honey")),
    BEE_HIVE_HONEY_HARVEST_FAIL_LORE(MessageContent.Multiple(listOf(
        "<gradient:light_purple:gold>Missing requirement:</gradient>",
        "{0}",
        "<gradient:light_purple:gold>Passed requirements:</gradient>",
        "{1}"
    ))),
    BEE_HIVE_HONEY_HARVEST_REQUIREMENTS_FAIL_TEMPLATE(MessageContent.Single("<gray>- <red>{0}")),
    BEE_HIVE_HONEY_HARVEST_LORE(MessageContent.Multiple(listOf(
        "<gradient:light_purple:gold>Requirements:</gradient>",
        "{0}"
    ))),
    BEE_HIVE_HONEY_HARVEST_REQUIREMENTS_TEMPLATE(MessageContent.Single("<gray>- <green>{0}")),


    BEE_HIVE_COMB_HARVEST_MENU_ICON(MessageContent.Single("<gold>Harvest combs")),
    BEE_HIVE_COMB_HARVEST_FAIL_LORE(MessageContent.Multiple(listOf(
        "<gradient:light_purple:gold>Missing requirement:</gradient>",
        "{0}",
        "<gradient:light_purple:gold>Passed requirements:</gradient>",
        "{1}"
    ))),
    BEE_HIVE_COMB_HARVEST_REQUIREMENTS_FAIL_TEMPLATE(MessageContent.Single("<gray>- <red>{0}")),
    BEE_HIVE_COMB_HARVEST_LORE(MessageContent.Multiple(listOf(
        "<gradient:light_purple:gold>Requirements:</gradient>",
        "{0}"
    ))),
    BEE_HIVE_COMB_HARVEST_REQUIREMENTS_TEMPLATE(MessageContent.Single("<gray>- <green>{0}")),

    ;


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