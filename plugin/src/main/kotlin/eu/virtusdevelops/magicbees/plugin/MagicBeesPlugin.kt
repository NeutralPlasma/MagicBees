package eu.virtusdevelops.magicbees.plugin

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.*
import eu.virtusdevelops.magicbees.api.models.Result
import eu.virtusdevelops.magicbees.core.controllers.*
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import eu.virtusdevelops.magicbees.core.storage.mysql.BeeHiveMysql
import eu.virtusdevelops.magicbees.core.utils.NBTUtil
import eu.virtusdevelops.magicbees.gui.GuiController
import eu.virtusdevelops.magicbees.plugin.commands.CommandRegistry
import eu.virtusdevelops.magicbees.plugin.listeners.BeeHiveBlockListener
import eu.virtusdevelops.magicbees.plugin.listeners.BeeInteractListener
import eu.virtusdevelops.magicbees.plugin.listeners.BlockInteractListener
import eu.virtusdevelops.magicbees.plugin.listeners.ChunkListener
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
import kotlin.math.log

class MagicBeesPlugin : JavaPlugin(), MagicBeesAPI {

    private lateinit var minecraftHelp: MinecraftHelp<Source>

    private lateinit var beehiveController: BeeHiveController
    private lateinit var providersController: ProvidersController
    private lateinit var requirementsController: RequirementsController
    private lateinit var rewardsController: RewardsController
    private lateinit var translationsController: TranslationsController
    private lateinit var storageController: StorageController

    private lateinit var guiController: GuiController

    private lateinit var blockInteractListener: BlockInteractListener

    override fun onEnable() {
        saveDefaultConfig()
        enableBStats()

        NBTUtil.load(this)
        registerControllers()
        // setup API
        MagicBeesAPI.setImplementation(this)
        // listeners
        setupListeners()
        // commands
        registerCommands()
    }

    override fun onDisable() {
        // storage save all
        storageController.shutdown()
        // unregister api
        MagicBeesAPI.unload()
        logger.info("Successfully disabled MagicBees!")
    }


    private fun registerControllers(){
        storageController = StorageController(this, logger)
        storageController.init()
        translationsController = TranslationsControllerImpl(logger, FileStorage(this, "translations.yml"))
        translationsController.init()
        providersController = ProvidersControllerImpl(this, logger)
        providersController.init()
        requirementsController = RequirementsControllerImpl(logger)
        requirementsController.init()
        rewardsController = RewardsControllerImpl(logger)
        rewardsController.init()
        beehiveController = BeeHiveControllerImpl(
            this,
            FileStorage(this, "levels.yml"),
            requirementsController,
            rewardsController,
            storageController,
            logger
        )
        beehiveController.init()
        guiController = GuiController(this)
        guiController.init()
    }

    private fun setupListeners(){
        val pm = this.server.pluginManager

        blockInteractListener = BlockInteractListener(this, beehiveController, translationsController)
        blockInteractListener.load()

        pm.registerEvents(BeeHiveBlockListener(logger, beehiveController, translationsController), this)
        pm.registerEvents(BeeInteractListener(logger, beehiveController), this)
        pm.registerEvents(blockInteractListener, this)
        pm.registerEvents(ChunkListener(beehiveController), this)
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

    override fun reload() : Result<List<String>, List<String>> {
        val result = mutableListOf<String>()
        // reload all controllers
        var start = System.currentTimeMillis()
        storageController.reload()
        var end = System.currentTimeMillis() - start
        result.add("<green>Reloaded StorageController in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()
        translationsController.reload()
        end = System.currentTimeMillis() - start
        result.add("<green>Reloaded TranslationsController in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()

        providersController.reload()
        end = System.currentTimeMillis() - start
        result.add("<green>Reloaded ProvidersController in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()

        requirementsController.reload()
        end = System.currentTimeMillis() - start
        result.add("<green>Reloaded RequirementsController in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()

        rewardsController.reload()
        end = System.currentTimeMillis() - start
        result.add("<green>Reloaded RewardsController in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()

        beehiveController.reload()
        end = System.currentTimeMillis() - start
        result.add("<green>Reloaded BeehiveController in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()

        guiController.reload()
        end = System.currentTimeMillis() - start
        result.add("<green>Reloaded GuiController in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()

        blockInteractListener.load()
        end = System.currentTimeMillis() - start
        result.add("<green>Reloaded BlockInteractListener in <yellow>${end}<green>ms")
        start = System.currentTimeMillis()
        return Result.Success(result)
    }
}