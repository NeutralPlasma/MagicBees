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
        setupDataSource()

        setupDaos()
        setupStorageControllers()
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
    }

    private fun setupStorageControllers(){
        beeHiveStorageController = BeeHiveStorageController(beeHiveDao, plugin.logger)
    }

    fun getBeeHiveStorage(): BeeHiveStorageController {
        return beeHiveStorageController
    }

}