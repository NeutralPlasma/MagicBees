package eu.virtusdevelops.magicbees.core.controllers

import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.mysql.BeeHiveMysql
import org.bukkit.plugin.java.JavaPlugin

class StorageController(private val plugin: JavaPlugin) {

    private lateinit var beeHiveDao: BeeHiveDao
    private lateinit var beeHiveStorageController: BeeHiveStorageController


    private lateinit var hikariDataSource: HikariDataSource

    fun init(){



        setupDaos()
        setupStorageControllers()
    }


    fun setupDaos(){

        beeHiveDao = BeeHiveMysql(hikariDataSource, plugin.logger)

    }


    fun setupStorageControllers(){
        beeHiveStorageController = BeeHiveStorageController(beeHiveDao, plugin.logger)
    }

    fun getBeeHiveStorage(): BeeHiveStorageController {
        return beeHiveStorageController
    }

}