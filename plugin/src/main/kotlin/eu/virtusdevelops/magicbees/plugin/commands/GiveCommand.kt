package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.core.utils.ItemUtils
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.parser.Parser
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
    @Command("magicbees give <player> <honey_level> <comb_level> <bee_amount>")
    @CommandDescription("Gives the player the beehive")
    fun giveCommand(
        ctx: Source,
        @Argument(value = "player", suggestions = "player") playerName: String,
        @Argument(value = "honey_level", suggestions = "honey_level") honeyLevel: Int,
        @Argument(value = "comb_level", suggestions = "comb_level") combLevel: Int,
        @Argument(value = "bee_amount") @Range(min = "0", max = "3")  beeAmount: Int
    ){

        val target = Bukkit.getPlayer(playerName)
        if(target == null){
            ctx.source().sendMessage(translationsController.getComponent(Messages.INVALID_PLAYER))
            return
        }

        val honeyLevels = beeHiveController.getHoneyLevels().map { it.level }
        if(!honeyLevels.contains(honeyLevel)){
            ctx.source().sendMessage(translationsController.getComponent(Messages.INVALID_HONEY_LEVEL, honeyLevel.toString()))
            return
        }

        val combLevels = beeHiveController.getCombLevels().map { it.level }
        if(!combLevels.contains(combLevel)){
            ctx.source().sendMessage(translationsController.getComponent(Messages.INVALID_COMB_LEVEL, combLevel.toString()))
            return
        }

        val item = beeHiveController.getBeeHiveItem(honeyLevel, combLevel, beeAmount)
        ItemUtils.give(target, item)
        ctx.source().sendMessage(translationsController.getComponent(Messages.BEE_HIVE_GIVEN, target.name))
        translationsController.sendMessage(target, Messages.BEE_HIVE_RECEIVED)
    }


    @Suggestions("player")
    fun getPlayers(sender: Source, input: String): List<String> {
        return Bukkit.getOnlinePlayers().map { it.name }
    }

    @Suggestions("honey_level")
    fun honeyLevels(sender: Source, input: String): List<String> {
        return plugin.getBeeHiveController().getHoneyLevels().map { it.level.toString() }
    }


    @Suggestions("comb_level")
    fun combLevels(sender: Source, input: String): List<String> {
        return plugin.getBeeHiveController().getCombLevels().map { it.level.toString() }
    }
}