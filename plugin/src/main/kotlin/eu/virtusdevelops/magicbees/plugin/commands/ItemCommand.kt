package eu.virtusdevelops.magicbees.plugin.commands

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.core.providers.ItemProvider
import eu.virtusdevelops.magicbees.core.utils.ItemUtils
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import org.bukkit.Bukkit
import org.incendo.cloud.annotations.*
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.paper.util.sender.Source

class ItemCommand : AbstractCommand {
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
    @Command("magicbees item <player> <item_key> <amount> ")
    @CommandDescription("Gives the player the beehive")
    fun itemCommand(
        ctx: Source,
        @Argument(value = "player", suggestions = "player") playerName: String,
        @Argument(value = "item_key", suggestions = "items") itemKey: String,
        @Argument(value = "amount") amount: Int,
    ) {

        val target = Bukkit.getPlayer(playerName)
        if (target == null) {
            ctx.source().sendMessage(translationsController.getComponent(Messages.INVALID_PLAYER))
            return
        }


        val provider = plugin.getProvidersController().getProvider("Item") ?: return
        if(provider !is ItemProvider) return

        val item = provider.getItem(itemKey)

        if(item == null){
            ctx.source().sendMessage(translationsController.getComponent(Messages.INVALID_ITEM, itemKey))
            return
        }

        ItemUtils.give(target, item, amount)

        ctx.source().sendMessage(translationsController.getComponent(Messages.ITEM_GIVEN, translationsController.getString(itemKey), target.name))
        translationsController.sendMessage(target, Messages.ITEM_RECEIVED, translationsController.getString(itemKey))

    }


    @Suggestions("player")
    fun getPlayers(sender: Source, input: String): List<String> {
        return Bukkit.getOnlinePlayers().map { it.name }
    }

    @Suggestions("items")
    fun getItems(sender: Source, input: String): List<String>{
        val provider = plugin.getProvidersController().getProvider("Item") ?: emptyList<String>()
        if(provider !is ItemProvider) return emptyList<String>()

        val items = provider.getAllItemKeys()

        return items
    }

}