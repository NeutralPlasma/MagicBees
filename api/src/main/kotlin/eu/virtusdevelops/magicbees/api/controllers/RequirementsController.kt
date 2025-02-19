package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.api.requirements.RequirementParser

interface RequirementsController {

    fun getAllParsers(): Set<RequirementParser>

    fun registerRequirementParser(parser: RequirementParser)

    fun getRequirements(data: List<String>): List<Requirement>
}