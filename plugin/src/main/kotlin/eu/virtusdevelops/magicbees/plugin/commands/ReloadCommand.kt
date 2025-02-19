package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.util.sender.Source
import javax.annotation.Nullable

class ReloadCommand : AbstractCommand {


    private lateinit var plugin: MagicBeesPlugin

    override fun registerCommands(plugin: MagicBeesPlugin, annotationParser: AnnotationParser<Source>) {
        annotationParser.parse(this)
        this.plugin = plugin
    }


    @Permission("magicbees.commands.reload")
    @Command("magicbees reload")
    @CommandDescription("Reloads the plugin")
    fun reloadCommand(
        ctx: Source
    ){
        MagicBeesAPI.get()?.reload()
    }
}