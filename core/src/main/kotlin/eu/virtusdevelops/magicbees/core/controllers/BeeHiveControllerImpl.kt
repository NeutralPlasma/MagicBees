package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.Location
import java.util.*

class BeeHiveControllerImpl : BeeHiveController {
    override fun initialize(): Boolean {
        TODO("Not yet implemented")
    }

    override fun saveBeeHive(beeHive: BeeHive) {
        TODO("Not yet implemented")
    }

    override fun removeBeeHive(beeHive: BeeHive) {
        TODO("Not yet implemented")
    }

    override fun getBeeHives(): Set<BeeHive> {
        TODO("Not yet implemented")
    }

    override fun getPlayerBeehives(playerUUID: UUID): Set<BeeHive> {
        TODO("Not yet implemented")
    }

    override fun getBeehive(beeHiveUUID: UUID): BeeHive? {
        TODO("Not yet implemented")
    }

    override fun getBeehive(location: Location): BeeHive? {
        TODO("Not yet implemented")
    }
}