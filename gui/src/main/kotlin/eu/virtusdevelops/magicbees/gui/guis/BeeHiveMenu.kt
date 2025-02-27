package eu.virtusdevelops.magicbees.gui.guis

import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.ListResult
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.api.models.Result
import eu.virtusdevelops.magicbees.gui.GUI
import eu.virtusdevelops.magicbees.gui.Icon
import net.kyori.adventure.key.Key.key
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
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
        val icon = Icon(harvestHoneyItem(beeHiveController.canHarvest(player, beeHive)), 5) { player, icon ->
            icon.setItemStack(harvestHoneyItem(beeHiveController.canHarvest(player, beeHive)))
        }
        icon.addClickAction {
            val result = beeHiveController.canHarvest(player, beeHive)
            if(result is Result.Failure){
                it.playSound(sound(key("block.vault.break"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
                return@addClickAction
            }
            // success
            beeHiveController.harvestBeeHive(player, beeHive)
            it.playSound(sound(key("block.beehive.drip"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
            refresh()
        }
        return icon
    }

    private fun harvestHoneyItem(result: Result<List<String>, ListResult<String, String>>): ItemStack {
        if(result is Result.Failure){
            val item = ItemStack(Material.BARRIER)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_HONEY_HARVEST_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_HONEY_HARVEST_FAIL_LORE
                )
            loreTemplate.forEach { template ->
                if(template == "{0}"){
                    result.errors.failed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_HONEY_HARVEST_REQUIREMENTS_FAIL_TEMPLATE, it))
                    }
                }else if(template == "{1}"){
                    result.errors.passed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_HONEY_HARVEST_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }else if(result is Result.Success){
            val item = ItemStack(Material.HONEY_BOTTLE)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_HONEY_HARVEST_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_HONEY_HARVEST_LORE
                )
            loreTemplate.forEachIndexed { index, template ->
                if(template == "{0}"){
                    result.value.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_HONEY_HARVEST_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }
        return ItemStack(Material.BARRIER)
    }


    // harvest comb

    private fun harvestCombIcon(): Icon {
        val icon = Icon(harvestCombItem(beeHiveController.canComb(player, beeHive)), 5) { player, icon ->
            icon.setItemStack(harvestCombItem(beeHiveController.canComb(player, beeHive)))
        }
        icon.addClickAction {
            val result = beeHiveController.canComb(player, beeHive)
            if(result is Result.Failure){
                it.playSound(sound(key("block.vault.break"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
                return@addClickAction
            }
            // success
            beeHiveController.combBeeHive(player, beeHive)
            it.playSound(sound(key("block.beehive.shear"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
            refresh()
        }
        return icon
    }


    private fun harvestCombItem(result: Result<List<String>, ListResult<String, String>>): ItemStack {

        if(result is Result.Failure){
            val item = ItemStack(Material.BARRIER)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_COMB_HARVEST_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_COMB_HARVEST_FAIL_LORE
                )
            loreTemplate.forEach { template ->
                if(template == "{0}"){
                    result.errors.failed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_COMB_HARVEST_REQUIREMENTS_FAIL_TEMPLATE, it))
                    }
                }else if(template == "{1}"){
                    result.errors.passed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_COMB_HARVEST_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }else if(result is Result.Success){
            val item = ItemStack(Material.HONEYCOMB)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_COMB_HARVEST_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_COMB_HARVEST_LORE
                )
            loreTemplate.forEach { template ->
                if(template == "{0}"){
                    result.value.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_COMB_HARVEST_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }
        return ItemStack(Material.BARRIER)
    }

    // upgrade honey

    private fun upgradeHoneyIcon(): Icon {
        val icon = Icon(upgradeHoneyItem(beeHiveController.canUpgradeHoneyLevel(player, beeHive)), 5) { player, icon ->
            icon.setItemStack(upgradeHoneyItem(beeHiveController.canUpgradeHoneyLevel(player, beeHive)))
        }
        icon.addClickAction {
            val result = beeHiveController.canUpgradeHoneyLevel(player, beeHive)
            if(result is Result.Failure || result is Result.Other ){
                it.playSound(sound(key("block.vault.break"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
                return@addClickAction
            }
            // success
            beeHiveController.upgradeHoneyLevel(player, beeHive)
            it.playSound(sound(key("block.vault.open_shutter"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
            refresh()
        }
        return icon
    }

    private fun upgradeHoneyItem(result: Result<List<String>, ListResult<String, String>>): ItemStack {
        if(result is Result.Failure){
            val item = ItemStack(Material.BARRIER)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_HONEY_UPGRADE_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_HONEY_LEVEL_UPGRADE_FAIL_LORE
                )
            loreTemplate.forEach { template ->
                if(template == "{0}"){
                    result.errors.failed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_HONEY_UPGRADE_FAIL_REQUIREMENTS_TEMPLATE, it))
                    }
                }else if(template == "{1}"){
                    result.errors.passed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_HONEY_UPGRADE_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }else if(result is Result.Success){
            val item = ItemStack(Material.EXPERIENCE_BOTTLE)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_HONEY_UPGRADE_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_HONEY_LEVEL_UPGRADE_LORE
                )
            loreTemplate.forEach { template ->
                if(template == "{0}"){
                    result.value.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_HONEY_UPGRADE_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }else if(result is Result.Other){
            val item = ItemStack(Material.SUNFLOWER)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_HONEY_UPGRADE_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_HONEY_MAXED_LORE
                )
            loreTemplate.forEach {  template ->
                loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }
        return ItemStack(Material.BARRIER)
    }


    // upgrade comb

    private fun upgradeCombIcon(): Icon {
        val icon = Icon(upgradeCombItem(beeHiveController.canUpgradeCombLevel(player, beeHive)), 5) { player, icon ->
            icon.setItemStack(upgradeCombItem(beeHiveController.canUpgradeCombLevel(player, beeHive)))
        }

        icon.addClickAction {
            val result = beeHiveController.canUpgradeCombLevel(player, beeHive)
            if(result is Result.Failure || result is Result.Other ){
                it.playSound(sound(key("block.vault.break"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
                return@addClickAction
            }
            // success
            beeHiveController.upgradeCombLevel(player, beeHive)
            it.playSound(sound(key("block.vault.open_shutter"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))
            refresh()
        }
        return icon
    }

    private fun upgradeCombItem(result: Result<List<String>, ListResult<String, String>>): ItemStack {
        if(result is Result.Failure){
            val item = ItemStack(Material.BARRIER)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_COMB_UPGRADE_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_COMB_LEVEL_UPGRADE_FAIL_LORE
                )
            loreTemplate.forEach { template ->
                if(template == "{0}"){
                    result.errors.failed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_COMB_UPGRADE_FAIL_REQUIREMENTS_TEMPLATE, it))
                    }
                }else if(template == "{1}"){
                    result.errors.passed.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_COMB_UPGRADE_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }else if(result is Result.Success){
            val item = ItemStack(Material.EXPERIENCE_BOTTLE)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_COMB_UPGRADE_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_COMB_LEVEL_UPGRADE_LORE
                )
            loreTemplate.forEach { template ->
                if(template == "{0}"){
                    result.value.forEach {
                        loreComponents.add(translationsController.getComponent(Messages.BEE_HIVE_COMB_UPGRADE_REQUIREMENTS_TEMPLATE, it))
                    }
                }else{
                    loreComponents.add(MiniMessage.miniMessage().deserialize(template).decorations(setOf(TextDecoration.ITALIC), false))
                }
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }else if(result is Result.Other){
            val item = ItemStack(Material.SUNFLOWER)
            val meta = item.itemMeta
            meta.displayName(translationsController.getComponent(Messages.BEE_HIVE_COMB_UPGRADE_MENU_ICON))
            val loreComponents = mutableListOf<Component>()
            val loreTemplate = translationsController
                .getStringList(
                    Messages.BEE_HIVE_COMB_MAXED_LORE
                )
            loreTemplate.forEach { it ->
                loreComponents.add(MiniMessage.miniMessage().deserialize(it).decorations(setOf(TextDecoration.ITALIC), false))
            }
            meta.lore(loreComponents)
            item.itemMeta = meta
            return item
        }
        return ItemStack(Material.BARRIER)
    }

}