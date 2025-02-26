package eu.virtusdevelops.magicbees.core.controllers

import eu.virtusdevelops.magicbees.api.controllers.Controller
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.ChunkLocation
import eu.virtusdevelops.magicbees.api.models.HiveLocation
import eu.virtusdevelops.magicbees.api.models.Position
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.ChunkData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

class BeeHiveStorageController(
    private val javaPlugin: JavaPlugin,
    private val dao: BeeHiveDao,
    private val logger: Logger
) : Controller{
    private var chunkData: HashMap<String, HashMap<ChunkLocation, ChunkData<BeeHive>>> = HashMap()


    private lateinit var executorService: ExecutorService
    private var savingSchedule: BukkitTask? = null


    override fun init(): Boolean {
        logger.info("Initializing beehive storage controller...")
        executorService = Executors.newFixedThreadPool(2)

        savingSchedule = Bukkit
            .getScheduler()
            .runTaskTimerAsynchronously(
                javaPlugin,
                Runnable { savingTask() },
                0, 1200)

        return true
    }

    override fun reload() {
        logger.info("Reloading beehive storage controller...")

        if(savingSchedule != null)
            savingSchedule!!.cancel()

        executorService.shutdown()
        executorService = Executors.newFixedThreadPool(2)

        savingSchedule = Bukkit
            .getScheduler()
            .runTaskTimerAsynchronously(
                javaPlugin,
                Runnable { savingTask() },
                0, 1200)
    }


    fun shutdown(){
        savingSchedule?.cancel()
        for(world in chunkData.keys){
            for(chunk in chunkData[world]!!.keys){
                saveChunk(chunkData[world]!![chunk]!!)
            }
        }
    }


    fun savingTask(){
        for(world in chunkData.keys){
            for(chunk in chunkData[world]!!.keys){
                saveChunkAsync(chunkData[world]!![chunk]!!)
            }
        }
    }

    fun get(location: Location): BeeHive? {
        val hiveLocation = HiveLocation.fromBukkitLocation(location)
        val world = location.world.name

        if(chunkData.containsKey(world)){
            if(chunkData[world]!!.containsKey(hiveLocation.chunkLocation)){
                return chunkData[world]!![hiveLocation.chunkLocation]?.getItem(hiveLocation.position)
            }
        }
        loadChunkAsync(hiveLocation.chunkLocation, world)
        return null
    }

    fun remove(beeHive: BeeHive): Boolean{
        if(chunkData[beeHive.location.worldName]!= null){
            if(chunkData[beeHive.location.worldName]!![beeHive.location.chunkLocation]!= null){
                if(chunkData[beeHive.location.worldName]!![beeHive.location.chunkLocation]?.removeItem(beeHive) == true){
                    return true
                }
            }
            return false
        }
        return false
    }

    fun store(beeHive: BeeHive): Boolean{
        val location = beeHive.location

        if(!chunkData.containsKey(location.worldName)){
            chunkData[location.worldName] = HashMap()
        }

        if(!chunkData[location.worldName]!!.containsKey(location.chunkLocation)){
            chunkData[location.worldName]!![location.chunkLocation] = ChunkData(location.chunkLocation)
        }

        chunkData[location.worldName]!![location.chunkLocation]?.addItem(beeHive)


        return true
    }



    fun loadChunkAsync(chunkLocation: ChunkLocation, world: String, callback: CallBack? = null){
        executorService.submit {
            loadChunk(chunkLocation, world)
            if (callback == null) return@submit

            Bukkit.getScheduler().runTask(javaPlugin, (Runnable {
                val data = chunkData[world]?.get(chunkLocation) ?: return@Runnable
                callback.run(data)
            }))
        }
    }

    fun loadChunk(chunkLocation: ChunkLocation, world: String){
        val loadedData = dao.getChunkData(chunkLocation.x, chunkLocation.z, world)



        val hivesNew = HashMap<Position, BeeHive>()

        for(beehive in loadedData){
            hivesNew[beehive.location.position] = beehive
        }
        val newChunkData = ChunkData(chunkLocation, hivesNew)

        if(!chunkData.containsKey(world)){
            chunkData[world] = HashMap()
        }

        chunkData[world]!![chunkLocation] = newChunkData
    }

    fun loadChunk(location: Location){
        loadChunk(HiveLocation.fromBukkitLocation(location).chunkLocation, location.world.name)
    }


    fun unloadChunk(location: Location){
        val hiveLocation = HiveLocation.fromBukkitLocation(location)
        unloadChunk(hiveLocation.chunkLocation, location.world.name)
    }


    fun unloadChunk(chunkLocation: ChunkLocation, world: String){
        val removedChunk = chunkData[world]?.remove(chunkLocation) ?: return
        saveChunkAsync(removedChunk)
    }

    fun saveChunkAsync(chunk: ChunkData<BeeHive>){
        executorService.submit {
            saveChunk(chunk)
        }
    }

    fun saveChunk(chunk: ChunkData<BeeHive>){

        for(beehive in chunk.removedItems.values){
            dao.delete(beehive)
        }

        for(beehive in chunk.addedItems.values){
            dao.save(beehive)
        }

        for(beehive in chunk.items.values){
            if(beehive.updated)
                dao.save(beehive)
        }

    }


}