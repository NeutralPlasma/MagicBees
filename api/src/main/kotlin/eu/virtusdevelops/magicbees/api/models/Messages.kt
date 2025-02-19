package eu.virtusdevelops.magicbees.api.models

enum class Messages(val defaultMessage: String) {
    HIVE_EMPTY("<red>Beehive is empty"),
    MISSING_REQUIREMENT("<red>You''re missing<gold> {0}<red> {1}"),
    SUCCESSFUL_HARVEST("<green>You successfully harvested"),

    // GUI
    BEE_HIVE_MENU_TITLE("<gradient:light_purple:gold>Magicbees Beehive</gradient>"),
    BEE_HIVE_STATUS_ICON_TITLE("<gradient:light_purple:gold>Beehive status</gradient>"),
    BEE_HIVE_STATUS_ICON_LORE("<gray>Honey upgrade level: <gold>{0};<gray>Comb upgrade level: <gold>{1};<gray>Bees: <gold>{2};<gray>Fullness: <gold>{3}")
}