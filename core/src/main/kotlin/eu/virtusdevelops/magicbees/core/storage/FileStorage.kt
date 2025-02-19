package eu.virtusdevelops.magicbees.core.storage

import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files

class FileStorage(private val javaPlugin: JavaPlugin, val filePath: String) {

    private var configuration: FileConfiguration? = null

    fun loadData(){
        val file = File(javaPlugin.dataFolder, filePath)
        if(!file.exists()){
            try{
                copy(javaPlugin.getResource(filePath)!!, Files.newOutputStream(file.toPath()))
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        configuration = YamlConfiguration()
        try {
            configuration!!.load(file)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    fun saveData(){
        val file = File(javaPlugin.dataFolder, filePath)
        if(!file.exists()){
            try{
                configuration!!.save(file)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun getConfiguration(): FileConfiguration? {
        return configuration
    }


    fun copy(input: InputStream, output: OutputStream){
        var n: Int
        val buffer = ByteArray(1024 * 4)

        try {
            while ((input.read(buffer).also { n = it }) != -1) {
                output.write(buffer, 0, n)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}