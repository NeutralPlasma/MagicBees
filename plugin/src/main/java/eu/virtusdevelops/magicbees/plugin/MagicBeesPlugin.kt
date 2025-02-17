package eu.virtusdevelops.magicbees.plugin



import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class MagicBeesPlugin : JavaPlugin(), MagicBeesAPI {

    override fun onEnable() {
        enableBStats()

        // setup API
        MagicBeesAPI.setImplementation(this)
    }

    override fun onDisable() {

    }



    fun enableBStats(){
        val pluginId = 19226
        val metrics = Metrics(this, pluginId)
    }
}