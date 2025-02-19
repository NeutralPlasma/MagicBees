package eu.virtusdevelops.magicbees.plugin

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.*
import eu.virtusdevelops.magicbees.core.controllers.*
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import eu.virtusdevelops.magicbees.core.storage.mysql.BeeHiveMysql
import eu.virtusdevelops.magicbees.gui.GuiController
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

class MagicBeesPlugin : JavaPlugin(), MagicBeesAPI {

    private lateinit var minecraftHelp: MinecraftHelp<Source>

    private lateinit var beehiveController: BeeHiveController
    private lateinit var providersController: ProvidersController
    private lateinit var requirementsController: RequirementsController
    private lateinit var rewardsController: RewardsController
    private lateinit var translationsController: TranslationsController

    private lateinit var guiController: GuiController

    override fun onEnable() {
        saveDefaultConfig()
        enableBStats()

        // todo: setup storage

        registerControllers()
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


    private fun registerControllers(){
        translationsController = TranslationsControllerImpl()
        providersController = ProvidersControllerImpl(this)
        requirementsController = RequirementsControllerImpl()
        rewardsController = RewardsControllerImpl()

        beehiveController = BeeHiveControllerImpl(
            FileStorage(this, "levels.yml"),
            BeeHiveMysql(),
            requirementsController,
            rewardsController
        )
        beehiveController.initialize()

        guiController = GuiController(this)
        guiController.init()

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
        return beehiveController
    }

    override fun getProvidersController(): ProvidersController {
        return providersController
    }

    override fun getRequirementsController(): RequirementsController {
        return requirementsController
    }

    override fun getRewardsController(): RewardsController {
        return rewardsController
    }

    override fun getTranslationsController(): TranslationsController {
        return translationsController
    }

    override fun reload() {
        providersController.getAll().forEach {  it.init() }
        beehiveController.initialize()
    }
}