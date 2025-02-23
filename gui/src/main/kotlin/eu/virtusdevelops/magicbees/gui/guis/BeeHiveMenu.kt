package eu.virtusdevelops.magicbees.gui.guis

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.api.models.Result
import eu.virtusdevelops.magicbees.gui.GUI
import eu.virtusdevelops.magicbees.gui.Icon
import net.kyori.adventure.key.Key.key
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class BeeHiveMenu(
    private val player: Player,
    private val beeHive: BeeHive
) : GUI(player, 36, MagicBeesAPI.get()!!.getTranslationsController().getString(Messages.BEE_HIVE_MENU_TITLE)) {

    private val beeHiveController: BeeHiveController = MagicBeesAPI.get()!!.getBeeHiveController()
    private val translationsController: TranslationsController = MagicBeesAPI.get()!!.getTranslationsController()

    init {
        setup()
        open()
    }


    private fun setup(){


        addIcon(13, statusIcon())
        addIcon(10, harvestHoneyIcon())
        addIcon(16, harvestCombIcon())

        addIcon(20, upgradeHoneyIcon())
        addIcon(24, upgradeCombIcon())

        // cant harvest empty

        // maxed upgrade honey
        // maxed upgrade comb
    }





    // status

    private fun statusIcon(): Icon {
        val icon = Icon(statusItem(), 5) { player, icon ->
            icon.setItemStack(statusItem())
        }
        icon.addClickAction {
            it.playSound(sound(key("ui.button.click"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
        }
        return icon
    }


    private fun statusItem(): ItemStack {
        val item = ItemStack(Material.BEEHIVE)
        val meta = item.itemMeta

        // parse title
        val title = translationsController.getComponent(Messages.BEE_HIVE_STATUS_ICON_TITLE)
        meta.displayName(title)
        // parse lore
        val lore = translationsController.getComponentList(
            Messages.BEE_HIVE_STATUS_ICON_LORE,
            beeHive.honeyUpgradeLevel.toString(),
            beeHive.honeyCombUpgradeLevel.toString(),
            beeHive.bees.toString(),
            beeHive.fullnessStatus.toString()
        )
        meta.lore(lore)
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)



        item.itemMeta = meta

        return item
    }



    // harvest honey

    private fun harvestHoneyIcon(): Icon {
        val icon = Icon(harvestHoneyItem(), 5) { player, icon ->
            icon.setItemStack(harvestHoneyItem())
        }
        icon.addClickAction {
            it.playSound(sound(key("ui.button.click"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
        }
        return icon
    }

    private fun harvestHoneyItem(): ItemStack {
        return ItemStack(Material.STONE)
    }


    // harvest comb

    private fun harvestCombIcon(): Icon {
        val icon = Icon(harvestCombItem(), 5) { player, icon ->
            icon.setItemStack(harvestCombItem())
        }
        icon.addClickAction {
            it.playSound(sound(key("ui.button.click"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
        }
        return icon
    }


    private fun harvestCombItem(): ItemStack {
        return ItemStack(Material.STONE)
    }

    // upgrade honey

    private fun upgradeHoneyIcon(): Icon {



        val icon = Icon(upgradeHoneyItem(beeHiveController.canUpgradeHoneyLevel(player, beeHive)), 5) { player, icon ->
            icon.setItemStack(upgradeHoneyItem(beeHiveController.canUpgradeHoneyLevel(player, beeHive)))
        }
        icon.addClickAction {
            val result = beeHiveController.canUpgradeHoneyLevel(player, beeHive)


            it.playSound(sound(key("ui.button.click"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
        }
        return icon
    }

    private fun upgradeHoneyItem(result: Result<Boolean, List<String>>): ItemStack {
        if(result is Result.Failure){
            val item = ItemStack(Material.BARRIER)
            val meta = item.itemMeta

            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_HONEY_UPGRADE_MENU_ICON))

            val lore = mutableListOf<String>()
            result.errors.forEach {
                lore.add(translationsController.getString(Messages.BEE_HIVE_HONEY_UPGRADE_FAIL_REQUIREMENTS_TEMPLATE, it))
            }
            val loreComponents = translationsController
                .getComponentList(
                    Messages.BEE_HIVE_HONEY_LEVEL_UPGRADE_FAIL_LORE,
                    lore.joinToString("\n")
                )

            meta.lore(loreComponents)

            item.itemMeta = meta
            return item
        }else{

        }


        return ItemStack(Material.STONE)
    }


    // upgrade comb

    private fun upgradeCombIcon(): Icon {
        val icon = Icon(upgradeCombItem(), 5) { player, icon ->
            icon.setItemStack(upgradeCombItem())
        }
        icon.addClickAction {
            it.playSound(sound(key("ui.button.click"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
        }
        return icon
    }

    private fun upgradeCombItem(): ItemStack {
        return ItemStack(Material.STONE)
    }

}