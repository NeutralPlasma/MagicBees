package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.Location
import org.bukkit.entity.Player
import java.util.UUID

interface BeeHiveController {

    fun initialize(): Boolean

    fun saveBeeHive(beeHive: BeeHive)

    fun removeBeeHive(beeHive: BeeHive)

    fun getBeeHives(): Set<BeeHive>

    fun getPlayerBeehives(playerUUID: UUID): Set<BeeHive>

    fun getBeehive(beeHiveUUID: UUID): BeeHive?

    fun getBeehive(location: Location): BeeHive?

    fun harvestBeeHive(player: Player, beeHive: BeeHive): Boolean

    fun combBeeHive(player: Player, beeHive: BeeHive): Boolean
}