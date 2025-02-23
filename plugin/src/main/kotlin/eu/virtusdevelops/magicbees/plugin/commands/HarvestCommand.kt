package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.gui.guis.BeeHiveMenu
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
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

        val location = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
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
                BeeHiveMenu(ctx.source() as Player, beehive)
            }
        }

    }


    @Suggestions("action")
    fun getActions(sender: Source, input: String): List<String> {
        return listOf("honey", "comb")
    }
}