package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.paper.util.sender.Source

interface AbstractCommand {
    fun registerCommands(
        plugin: MagicBeesPlugin,
        annotationParser: AnnotationParser<Source>
    )
}