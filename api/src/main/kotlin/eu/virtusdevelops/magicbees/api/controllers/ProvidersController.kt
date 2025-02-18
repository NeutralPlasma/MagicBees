package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.Provider

interface ProvidersController {

    fun registerProvider(provider: Provider<*>)

    fun unregisterProvider(provider: Provider<*>)

    fun getProvider(providerType: Class<out Provider<*>>): Provider<*>?

    fun getProvider(providerType: String): Provider<*>?

    fun getProvider(returnType: Class<*>): Set<Provider<*>>?
}