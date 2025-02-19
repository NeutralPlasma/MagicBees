package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import net.kyori.adventure.text.minimessage.MiniMessage
import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.paper.util.sender.Source
import javax.annotation.Nullable

class DebugCommand : AbstractCommand {

    private lateinit var plugin: MagicBeesPlugin

    override fun registerCommands(plugin: MagicBeesPlugin, annotationParser: AnnotationParser<Source>) {
        annotationParser.parse(this)
        this.plugin = plugin
    }

    @Permission("magicbees.commands.debug")
    @Command("magicbees debug [query]")
    @CommandDescription("Gives some debug information")
    fun debugCommand(
        ctx: Source,
        @Argument(value = "query", suggestions = "action") @Nullable query: String?
    ){
        var action = "honey"
        if(query != null){
            action = query
        }

        val source = ctx.source()
        val mm = MiniMessage.miniMessage()

        when (action){
            "providers" -> {
                val providers = MagicBeesAPI.get()!!.getProvidersController().getAll()
                source.sendMessage(mm.deserialize("<gradient:light_purple:gold>MagicBees Providers:</gradient>"))
                for(provider in providers) {
                    source.sendMessage(mm.deserialize("<gray>- <gold>${provider.getName()}"))
                }
            }
            "reward_parsers" -> {
                val parsers = MagicBeesAPI.get()!!.getRewardsController().getAllParsers()
                source.sendMessage(mm.deserialize("<gradient:light_purple:gold>MagicBees reward parsers:</gradient>"))
                for(parser in parsers) {
                    source.sendMessage(mm.deserialize("<gray>- <gold>${parser.getName()}"))
                }
            }
            "requirements_parsers" -> {
                val parsers = MagicBeesAPI.get()!!.getRequirementsController().getAllParsers()
                source.sendMessage(mm.deserialize("<gradient:light_purple:gold>MagicBees requirements parsers:</gradient>"))
                for(parser in parsers) {
                    source.sendMessage(mm.deserialize("<gray>- <gold>${parser.getName()}"))
                }
            }
            else -> {
                ctx.source().sendMessage("Invalid action!")
            }
        }

    }



    @Suggestions("action")
    fun getActions(sender: Source, input: String): List<String> {
        return listOf("providers", "reward_parsers", "requirements_parsers")
    }
}