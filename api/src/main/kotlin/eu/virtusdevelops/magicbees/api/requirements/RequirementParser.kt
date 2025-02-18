package eu.virtusdevelops.magicbees.api.requirements

interface RequirementParser {

    fun getName(): String

    fun parse(data: String): Requirement
}