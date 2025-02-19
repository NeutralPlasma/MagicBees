package eu.virtusdevelops.magicbees.api.models

enum class Messages(val defaultMessage: String) {
    HIVE_EMPTY("<red>Beehive is empty"),
    MISSING_REQUIREMENT("<red>You''re missing<gold> {0}<red> {1}"),
    SUCCESSFUL_HARVEST("<green>You successfully harvested"),
}