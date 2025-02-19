package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.RequirementsController
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.core.requirements.*

class RequirementsControllerImpl: RequirementsController {

    private val requirementParsers: MutableMap<String, RequirementParser> = mutableMapOf()

    init {
        registerRequirementParser(ItemRequirementParser())
        registerRequirementParser(VaultRequirementParser())
        registerRequirementParser(VotingPluginRequirementParser())
        registerRequirementParser(ExperienceRequirementParser())
        registerRequirementParser(CoinsEngineRequirementParser())
    }


    override fun registerRequirementParser(parser: RequirementParser) {
        requirementParsers[parser.getName()] = parser
    }

    override fun getRequirements(data: List<String>): List<Requirement> {
        val requirements: MutableList<Requirement> = mutableListOf()
        for(line in data) {
            requirements.add(requirementParsers[line.substringBefore(":")]?.parse(line) ?: continue)
        }
        return requirements
    }

    override fun getAllParsers(): Set<RequirementParser> = requirementParsers.values.toSet()
}