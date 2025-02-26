package eu.virtusdevelops.magicbees.core.controllers

import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.magicbees.api.controllers.Controller
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import eu.virtusdevelops.magicbees.core.storage.mysql.BeeHiveMysql
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class StorageController(private val plugin: JavaPlugin,
    private val logger: Logger) : Controller {

    private lateinit var beeHiveDao: BeeHiveDao
    private lateinit var beeHiveStorageController: BeeHiveStorageController


    private lateinit var hikariDataSource: HikariDataSource

    override fun init(): Boolean{
        try{
            logger.info("Initializing storage controller...")
            setupDataSource()
            setupDaos()
            setupStorageControllers()
        }catch (e: Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun reload() {
        try{
            setupDataSource()
            setupDaos()
            beeHiveStorageController.reload()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun shutdown(){
        beeHiveStorageController.shutdown()
    }

    fun setupDataSource(){
        val config = plugin.config

        when (config.getString("database.driver")!!.lowercase()){
            "mysql" -> {
                hikariDataSource = HikariDataSource().apply {
                    jdbcUrl = "jdbc:mysql://${config.getString("database.host")}:${config.getInt("database.port")}/${config.getString("database.database")}"
                    username = config.getString("database.username")
                    password = config.getString("database.password")
                    driverClassName = "com.mysql.jdbc.Driver"
                }
            }
            "mariadb" -> {
                hikariDataSource = HikariDataSource().apply {
                    jdbcUrl = "jdbc:mariadb://${config.getString("database.host")}:${config.getInt("database.port")}/${config.getString("database.database")}"
                    username = config.getString("database.username")
                    password = config.getString("database.password")
                    driverClassName = "org.mariadb.jdbc.Driver"
                }
            }
            "h2" -> {
                hikariDataSource = HikariDataSource().apply {
                    jdbcUrl = "jdbc:h2:file:${plugin.dataFolder.absolutePath}/magicbees.db"
                    driverClassName = "org.h2.Driver"
                }
            }
            else -> throw IllegalArgumentException("Invalid database driver!")
        }


    }


    private fun setupDaos(){
        beeHiveDao = BeeHiveMysql(hikariDataSource, plugin.logger)
        beeHiveDao.init()
    }

    private fun setupStorageControllers(){
        beeHiveStorageController = BeeHiveStorageController(plugin, beeHiveDao, plugin.logger)
        beeHiveStorageController.init()
    }

    fun getBeeHiveStorage(): BeeHiveStorageController {
        return beeHiveStorageController
    }

}