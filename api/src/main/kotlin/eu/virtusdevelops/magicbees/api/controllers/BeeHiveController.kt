package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.ChunkLocation
import eu.virtusdevelops.magicbees.api.models.Result
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface BeeHiveController {

    fun initialize(): Boolean

    fun saveBeeHive(beeHive: BeeHive)

    fun removeBeeHive(beeHive: BeeHive)

    fun getBeehive(location: Location): BeeHive?

    fun canHarvest(player: Player, beeHive: BeeHive): Result<Boolean, List<String>>

    fun canComb(player: Player, beeHive: BeeHive): Result<Boolean, List<String>>

    fun canUpgradeHoneyLevel(player: Player, beeHive: BeeHive): Result<Boolean, List<String>>

    fun canUpgradeCombLevel(player: Player, beeHive: BeeHive): Result<Boolean, List<String>>

    fun harvestBeeHive(player: Player, beeHive: BeeHive): Boolean

    fun combBeeHive(player: Player, beeHive: BeeHive): Boolean

    fun upgradeHoneyLevel(player: Player, beeHive: BeeHive): Boolean

    fun upgradeCombLevel(player: Player, beeHive: BeeHive): Boolean

    fun loadChunk(chunk: ChunkLocation, world: String)

    fun unloadChunk(chunk: ChunkLocation, world: String)

    fun getBeeHiveItem(honeyLevel: Int, combLevel: Int): ItemStack
}