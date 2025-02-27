package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.api.models.Result
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import net.kyori.adventure.text.minimessage.MiniMessage
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
    @CommandDescription("Reloads configuration of the plugin")
    fun reloadCommand(
        ctx: Source
    ){
        val start = System.currentTimeMillis()
        val result = plugin.reload()
        val total = System.currentTimeMillis() - start

        if(result is Result.Success){
            result.value.forEach {
                ctx.source().sendMessage(MiniMessage.miniMessage().deserialize(it))
            }
        }

        ctx.source().sendMessage(plugin.getTranslationsController().getComponent(Messages.SUCCESSFULLY_RELOADED, total.toString()))
    }
}