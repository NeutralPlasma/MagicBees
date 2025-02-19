package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.models.Location
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.paper.util.sender.Source
import javax.annotation.Nullable

class HarvestCommand : AbstractCommand {

    private lateinit var plugin: MagicBeesPlugin

    override fun registerCommands(plugin: MagicBeesPlugin, annotationParser: AnnotationParser<Source>) {
        annotationParser.parse(this)
        this.plugin = plugin
    }



    @Permission("magicbees.commands.harvest")
    @Command("magicbees harvest [query]")
    @CommandDescription("Shows this menu")
    fun harvestCommand(
        ctx: Source,
        @Argument(value = "query", suggestions = "action") @Nullable query: String?
    ){
        var action = "honey"
        if(query != null){
            action = query
        }

        val location = Location(0, 0, 0, "world")
        val beeHiveController = MagicBeesAPI.get()!!.getBeeHiveController()
        val beehive = beeHiveController.getBeehive(location)

        if(beehive == null){
            ctx.source().sendMessage("Invalid beehive!")
            return
        }

        when(action) {
            "honey" -> {
                beeHiveController.harvestBeeHive(ctx.source() as Player, beehive)
            }
            "comb" -> {
                beeHiveController.combBeeHive(ctx.source() as Player, beehive)
            }
            else -> {
                ctx.source().sendMessage("Invalid action!")
            }
        }

    }


    @Suggestions("action")
    fun getActions(sender: Source, input: String): List<String> {
        return listOf("honey", "comb")
    }
}