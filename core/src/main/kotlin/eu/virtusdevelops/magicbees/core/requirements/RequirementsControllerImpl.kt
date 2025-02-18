package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.controllers.RequirementsController
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.core.requirements.parsers.ExperienceRequirementParser
import eu.virtusdevelops.magicbees.core.requirements.parsers.ItemRequirementParser
import eu.virtusdevelops.magicbees.core.requirements.parsers.VaultRequirementParser
import eu.virtusdevelops.magicbees.core.requirements.parsers.VotingPluginRequirementParser

class RequirementsControllerImpl: RequirementsController {

    init {
        registerRequirementParser(ItemRequirementParser())
        registerRequirementParser(VaultRequirementParser())
        registerRequirementParser(VotingPluginRequirementParser())
        registerRequirementParser(ExperienceRequirementParser())
    }

    private val requirementParsers: MutableMap<String, RequirementParser> = mutableMapOf()


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
}