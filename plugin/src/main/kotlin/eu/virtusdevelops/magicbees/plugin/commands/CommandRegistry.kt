package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.requirements.RequirementParser
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.incendo.cloud.CommandManager
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.paper.util.sender.Source

class CommandRegistry(private val plugin: MagicBeesPlugin, private val manager: CommandManager<Source>){
    private val annotationParser: AnnotationParser<Source> = AnnotationParser(manager, Source::class.java)

    private val commands: List<AbstractCommand> = listOf(
        HelpCommand(),
        HarvestCommand(),
        DebugCommand(),
        ReloadCommand(),
        GiveCommand()
    )

    init {
        registerCommands()
    }


    private fun registerCommands() {

        commands.forEach {
                command -> command.registerCommands(plugin, annotationParser)
        }

    }
}