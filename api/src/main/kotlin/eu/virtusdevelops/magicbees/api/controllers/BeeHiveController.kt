package eu.virtusdevelops.magicbees.api.controllers

import eu.virtusdevelops.magicbees.api.models.*
import eu.virtusdevelops.magicbees.api.rewards.Reward
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface BeeHiveController : Controller {

    /**
     * Saves the given beehive to persistent storage or updates its current state.
     *
     * @param beeHive The BeeHive instance to be saved or updated.
     * @return True if the beehive was successfully saved or updated; false otherwise.
     */
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

    fun getHoneyLevels(): Collection<BeeHiveLevel>

    fun getCombLevels(): Collection<BeeHiveLevel>

    fun getHarvestRewards(level: Int): Result<Collection<Reward>, List<String>>

    fun getCombRewards(level: Int): Result<Collection<Reward>, List<String>>
}