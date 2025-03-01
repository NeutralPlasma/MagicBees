package eu.virtusdevelops.magicbees.gui.guis

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.models.BeeHiveLevel
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.gui.GUI
import org.bukkit.entity.Player

class BeeHiveLevelMenu(
    private val player: Player,
    private val beeHiveLevel: BeeHiveLevel
) : GUI(player, 36, MagicBeesAPI.get()!!.getTranslationsController().getString(Messages.BEE_HIVE_MENU_TITLE)){


    init {
        setup()
        open()
    }


    private fun setup(){

    }


    // rewards icon


    // harvest requirements icon


    // upgrade requirements




}