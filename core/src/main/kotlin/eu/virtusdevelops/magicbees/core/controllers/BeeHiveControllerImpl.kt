package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.Location
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BeeHiveControllerImpl(private val dao: BeeHiveDao) : BeeHiveController {

    private val beehives: ConcurrentHashMap<Location, BeeHive> = ConcurrentHashMap()

    private val executorService: ExecutorService = Executors.newFixedThreadPool(2)


    override fun initialize(): Boolean {
        // load all beehives
        try{
            dao.getAll().forEach { beehives[it.location] = it }
        }catch (e: Exception){
            return false
        }
        return true
    }

    override fun saveBeeHive(beeHive: BeeHive) {

        executorService.submit {
            if(!dao.save(beeHive))
                beehives.remove(beeHive.location)
        }

        beehives[beeHive.location] = beeHive
    }

    override fun removeBeeHive(beeHive: BeeHive) {
        executorService.submit {
            if (!dao.delete(beeHive))
                beehives[beeHive.location] = beeHive
        }
        beehives.remove(beeHive.location)
    }

    override fun getBeeHives(): Set<BeeHive> {
        return beehives.values.toSet()
    }

    override fun getPlayerBeehives(playerUUID: UUID): Set<BeeHive> {
        val set: MutableSet<BeeHive> = mutableSetOf()
        for (beehive in beehives) {
            if(beehive.value.owner.equals(playerUUID)) set.add(beehive.value)
        }
        return set;
    }

    override fun getBeehive(beeHiveUUID: UUID): BeeHive? {
        for (beeHive in beehives.values) {
            if(beeHive.id.equals(beeHiveUUID)) return beeHive
        }
        return null
    }

    override fun getBeehive(location: Location): BeeHive? {
        return beehives[location]
    }

    override fun harvestBeeHive(player: Player, beeHive: BeeHive): Boolean {
        TODO("Not yet implemented")
    }

    override fun combBeeHive(player: Player, beeHive: BeeHive): Boolean {
        TODO("Not yet implemented")
    }

}