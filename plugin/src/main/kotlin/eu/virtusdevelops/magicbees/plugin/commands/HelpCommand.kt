package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.util.sender.Source
import javax.annotation.Nullable

class HelpCommand : AbstractCommand {


    private lateinit var plugin: MagicBeesPlugin

    override fun registerCommands(plugin: MagicBeesPlugin, annotationParser: AnnotationParser<Source>) {
        annotationParser.parse(this)
        this.plugin = plugin
    }


    @Permission("magicbees.commands.help")
    @Command("magicbees help [query]")
    @CommandDescription("Shows this menu")
    fun helpCommand(
        ctx: Source,
        @Argument("query") @Nullable query: Array<String>?
    ){
        plugin.getMinecraftHelp().queryCommands(if (query != null) java.lang.String.join(" ", *query) else "", ctx)
    }
}