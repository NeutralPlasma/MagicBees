package eu.virtusdevelops.magicbees.api

import eu.virtusdevelops.magicbees.api.controllers.*
import eu.virtusdevelops.magicbees.api.models.Result
import org.jetbrains.annotations.ApiStatus

interface MagicBeesAPI {

    companion object {
        private var implementation: MagicBeesAPI? = null
        private var enabled = false


        /**
         * Retrieves the current implementation of the MagicBeesAPI if it is enabled.
         *
         * @return The current implementation of MagicBeesAPI, or null if not set.
         * @throws IllegalStateException if the API is not enabled.
         */
        fun get(): MagicBeesAPI? {
            if (!enabled) {
                throw IllegalStateException("MagicBeesAPI is not enabled")
            }
            return implementation
        }


        /**
         * Sets the implementation instance for the MagicBeesAPI.
         * This method should be called before the API is enabled.
         *
         * @param impl The implementation of the MagicBeesAPI to be set.
         * @throws IllegalStateException if the MagicBeesAPI is already enabled.
         */
        @ApiStatus.Internal
        fun setImplementation(impl: MagicBeesAPI) {
            if (enabled) {
                throw IllegalStateException("MagicBeesAPI is already enabled")
            }
            implementation = impl
            enabled = true
        }

        @ApiStatus.Internal
        fun unload() {
            implementation = null
            enabled = false
        }
    }


    fun reload(): Result<List<String>, List<String>>

    fun getBeeHiveController(): BeeHiveController

    fun getProvidersController(): ProvidersController

    fun getRequirementsController(): RequirementsController

    fun getRewardsController(): RewardsController

    fun getTranslationsController(): TranslationsController
}