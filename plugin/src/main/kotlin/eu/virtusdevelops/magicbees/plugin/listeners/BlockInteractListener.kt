package eu.virtusdevelops.magicbees.plugin.listeners

import eu.virtusdevelops.magicbees.api.controllers.BeeHiveController
import eu.virtusdevelops.magicbees.api.controllers.TranslationsController
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.HiveLocation
import eu.virtusdevelops.magicbees.api.models.Messages
import eu.virtusdevelops.magicbees.core.providers.ItemProvider
import eu.virtusdevelops.magicbees.gui.guis.BeeHiveMenu
import eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin
import net.kyori.adventure.key.Key.key
import net.kyori.adventure.sound.Sound.sound
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

class BlockInteractListener(
    private val plugin: MagicBeesPlugin,
    private val beeHiveController: BeeHiveController,
    private val translationsController: TranslationsController) : Listener{

    private var harvestToolMaterial: Material = Material.AIR
    private var harvestCombToolMaterial: Material = Material.AIR
    private var conversionItem: ItemStack = ItemStack(Material.BEDROCK)


    fun load(){
        val config = plugin.config
        harvestToolMaterial = Material.matchMaterial(config.getString("quick_honey_harvest_tool") ?: "AIR") ?: Material.AIR
        harvestCombToolMaterial = Material.matchMaterial(config.getString("quick_comb_harvest_tool") ?: "AIR") ?: Material.AIR

        val provider = plugin.getProvidersController().getProvider("Item") ?: return
        if(provider !is ItemProvider) return

        conversionItem = provider.getItem(config.getString("conversion_item") ?: "") ?: ItemStack(Material.BEDROCK)

    }



    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockInteract(event: PlayerInteractEvent){
        val block = event.clickedBlock ?: return
        val blockLocation = event.clickedBlock?.location ?: return
        val player = event.player
        if(block.type != Material.BEEHIVE) return

        val item = event.item
        val material = item?.type ?: Material.AIR



        val hive = beeHiveController.getBeehive(blockLocation)

        if(hive == null && material != Material.AIR){

            if(item?.isSimilar(conversionItem) == true){
                event.isCancelled = true

                if(!event.player.hasPermission("magicbees.beehive.convert")){
                    translationsController.sendMessage(event.player, Messages.NO_PERMISSION, "magicbees.beehive.convert")
                    return
                }

                // take the item
                val provider = plugin.getProvidersController().getProvider("Item") ?: return
                if(provider !is ItemProvider) return
                provider.take(player, plugin.config.getString("conversion_item") ?: "")

                // convert the beehive

                val state = block.state
                var bees = 0
                if(state is org.bukkit.block.Beehive){
                    bees = state.entityCount
                }

                val beehive = BeeHive(
                    id = UUID.randomUUID(),
                    location = HiveLocation.fromBukkitLocation(block.location),
                    owner = event.player.uniqueId,
                    createdTime = System.currentTimeMillis(),
                    honeyUpgradeLevel = 1,
                    honeyCombUpgradeLevel = 1,
                    bees = bees
                )
                beeHiveController.saveBeeHive(beehive)
                translationsController.sendMessage(event.player, Messages.BEE_HIVE_CONVERTED)
                player.playSound(sound(key("block.vault.open_shutter"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.19f))

                return
            }

        }
        if(hive == null){
            return
        }

        // process the click

        if(event.action != Action.RIGHT_CLICK_BLOCK){
            return
        }

        event.isCancelled = true
        if(event.hand != EquipmentSlot.HAND){
            return
        }

        if(!event.player.hasPermission("magicbees.beehive.interact")){
            translationsController.sendMessage(event.player, Messages.NO_PERMISSION, "magicbees.beehive.interact")
            return
        }




        if(material == harvestToolMaterial){
            beeHiveController.harvestBeeHive(player, hive)
            return
        }

        if(material == harvestCombToolMaterial){
            beeHiveController.combBeeHive(player, hive)
            return
        }




        if(item == null){
            BeeHiveMenu(player, hive).open()
            return
        }




    }


}