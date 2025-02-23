package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.core.utils.ItemUtils
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.paper.util.sender.Source

class GiveCommand : AbstractCommand {
    private lateinit var plugin: MagicBeesPlugin
    private lateinit var translationsController: TranslationsController
    private lateinit var beeHiveController: BeeHiveController

    override fun registerCommands(plugin: MagicBeesPlugin, annotationParser: AnnotationParser<Source>) {
        this.plugin = plugin
        beeHiveController = plugin.getBeeHiveController()
        translationsController = plugin.getTranslationsController()
        annotationParser.parse(this)
    }


    @Permission("magicbees.commands.give")
    @Command("magicbees give <player> <honey_level> <comb_level>")
    @CommandDescription("Gives the player the beehive")
    fun giveCommand(
        ctx: Source,
        @Argument(value = "player", suggestions = "player") playerName: String,
        @Argument(value = "honey_level") honeyLevel: Int,
        @Argument(value = "comb_level") combLevel: Int
    ){

        val target = Bukkit.getPlayer(playerName)
        if(target == null){
            ctx.source().sendMessage(translationsController.getComponent(Messages.INVALID_PLAYER))
            return
        }


        val item = beeHiveController.getBeeHiveItem(honeyLevel, combLevel)
        ItemUtils.give(target, item)
    }


    @Suggestions("player")
    fun getPlayers(sender: Source, input: String): List<String> {
        return Bukkit.getOnlinePlayers().map { it.name }
    }
}