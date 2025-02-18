package eu.virtusdevelops.magicbees.plugin

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.ProvidersController
import eu.virtusdevelops.magicbees.plugin.commands.CommandRegistry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.minecraft.extras.AudienceProvider
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.MinecraftHelp
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper
import org.incendo.cloud.paper.util.sender.Source
import org.incendo.cloud.setting.ManagerSetting
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MagicBeesPlugin : JavaPlugin(), MagicBeesAPI {

    private lateinit var minecraftHelp: MinecraftHelp<Source>

    override fun onEnable() {
        saveDefaultConfig()
        enableBStats()

        // setup API
        MagicBeesAPI.setImplementation(this)

        // commands
        registerCommands()
    }

    override fun onDisable() {
        // storage save all
        // unregister api
        MagicBeesAPI.unload()
        logger.info("Successfully disabled MagicBees!")
    }



    private fun registerCommands(){

        val manager : PaperCommandManager<Source> = PaperCommandManager
            .builder(PaperSimpleSenderMapper.simpleSenderMapper())
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(this)

        manager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true)

        manager.exceptionController().clearHandlers()


        val audienceProvider = AudienceProvider<Source> { source: Source -> source.source()  }

        registerHelpCommand(audienceProvider, manager)


        MinecraftExceptionHandler.create(audienceProvider)
            .defaultHandlers()
            .decorator { component: Component? ->
                Component.text()
                    .append(Component.text("[", NamedTextColor.DARK_GRAY))
                    .append(Component.text("MagicBees", NamedTextColor.GOLD))
                    .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                    .append(component!!)
                    .build()
            }
            .registerTo(manager)

        manager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider())

        CommandRegistry(this, manager)
    }

    private fun registerHelpCommand(audienceProvider: AudienceProvider<Source>, commandManager: PaperCommandManager<Source>){
        minecraftHelp = MinecraftHelp.builder<Source>()
            .commandManager(commandManager)
            .audienceProvider(audienceProvider)
            .commandPrefix("/magicbees help")
            .messageProvider(MinecraftHelp.captionMessageProvider(
                commandManager.captionRegistry(),
                ComponentCaptionFormatter.miniMessage()
            ))
            .build()
    }

    fun getMinecraftHelp(): MinecraftHelp<Source> {
        return minecraftHelp
    }



    fun enableBStats(){
        if(!config.getBoolean("metrics")) return
        logger.info(String.format("Enabling BStats metrics for MagicBees..."))
        val pluginId = 24838
        val metrics = Metrics(this, pluginId)
    }


    override fun getBeeHiveController(): BeeHiveController {
        TODO("Not yet implemented")
    }

    override fun getProvidersController(): ProvidersController {
        TODO("Not yet implemented")
    }

}