package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.ChunkLocation
import eu.virtusdevelops.magicbees.api.models.ListResult
import eu.virtusdevelops.magicbees.api.models.Result
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface BeeHiveController : Controller {

    fun saveBeeHive(beeHive: BeeHive): Boolean

    fun removeBeeHive(beeHive: BeeHive): Boolean

    fun getBeehive(location: Location): BeeHive?

    fun canHarvest(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>>

    fun canComb(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>>

    fun canUpgradeHoneyLevel(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>>

    fun canUpgradeCombLevel(player: Player, beeHive: BeeHive): Result<List<String>, ListResult<String, String>>

    fun harvestBeeHive(player: Player, beeHive: BeeHive): Boolean

    fun combBeeHive(player: Player, beeHive: BeeHive): Boolean

    fun upgradeHoneyLevel(player: Player, beeHive: BeeHive): Boolean

    fun upgradeCombLevel(player: Player, beeHive: BeeHive): Boolean

    fun loadChunk(chunk: ChunkLocation, world: String)

    fun unloadChunk(chunk: ChunkLocation, world: String)

    fun getBeeHiveItem(honeyLevel: Int, combLevel: Int, beeAmount: Int): ItemStack

}