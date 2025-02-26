package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.RequirementsController
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.core.requirements.*
import java.util.logging.Logger

class RequirementsControllerImpl(
    private val logger: Logger
): RequirementsController {

    private val requirementParsers: MutableMap<String, RequirementParser> = mutableMapOf()


    override fun init(): Boolean {
        logger.info("Initializing requirements controller...")
        registerRequirementParser(ItemRequirementParser())
        registerRequirementParser(VaultRequirementParser())
        registerRequirementParser(VotingPluginRequirementParser())
        registerRequirementParser(ExperienceRequirementParser())
        registerRequirementParser(CoinsEngineRequirementParser())
        registerRequirementParser(DurabilityRequirementParser())
        return true
    }

    override fun reload() {

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