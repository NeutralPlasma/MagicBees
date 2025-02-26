package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.Provider
import eu.virtusdevelops.magicbees.api.controllers.ProvidersController
import eu.virtusdevelops.magicbees.core.providers.*
import eu.virtusdevelops.magicbees.core.storage.FileStorage
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class ProvidersControllerImpl(private val javaPlugin: JavaPlugin,
    private val logger: Logger) : ProvidersController {

    private val providers: MutableMap<String, Provider<*>> = mutableMapOf()


    override fun init(): Boolean {
        logger.info("Initializing providers controller...")
        registerProvider(ExperienceProvider())
        registerProvider(ItemDamageProvider())
        registerProvider(ItemProvider(FileStorage(javaPlugin, "items.yml"), javaPlugin.logger))


        val pm = Bukkit.getPluginManager()
        if(pm.isPluginEnabled("VotingPlugin"))
            registerProvider(VotingPluginProvider())
        if(pm.isPluginEnabled("CoinsEngine"))
            registerProvider(CoinsEngineProvider())
        if(pm.isPluginEnabled("Vault"))
            registerProvider(VaultProvider())

        reload()

        return true
    }

    override fun reload() {
        // reload default providers?
        providers.forEach {
            it.value.init()
        }
    }

    override fun registerProvider(provider: Provider<*>) {
        provider.init()
        providers[provider.getName()] = provider
    }

    override fun unregisterProvider(provider: Provider<*>) {
        if(providers.containsKey(provider.getName()))
            providers.remove(provider.getName())
    }

    override fun getProvider(providerType: Class<out Provider<*>>): Provider<*>? {
        return providers[providerType.simpleName]
    }

    override fun getProvider(providerType: String): Provider<*>? {
        return providers[providerType]
    }

    override fun getProvider(returnType: Class<*>): Set<Provider<*>>? {
        // todo
        return null
    }


    override fun getAll(): Set<Provider<*>> = providers.values.toSet()
}