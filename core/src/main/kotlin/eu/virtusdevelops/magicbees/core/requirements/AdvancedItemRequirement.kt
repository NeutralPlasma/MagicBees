package eu.virtusdevelops.magicbees.core.requirements

import eu.virtusdevelops.magicbees.api.AdvancedProvider
import eu.virtusdevelops.magicbees.api.MagicBeesAPI
import eu.virtusdevelops.magicbees.api.requirements.Requirement
import eu.virtusdevelops.magicbees.core.providers.ItemProvider
import it.unimi.dsi.fastutil.booleans.BooleanIntMutablePair
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AdvancedItemRequirement(
    private val key: String,
    private val amount: Int,
    private val provider: AdvancedProvider<ItemStack, Int>,
    private val itemProvider: ItemProvider,
    private val ignoreEnchants: Boolean,
    private val ignoreLore: Boolean) : Requirement {



    override fun check(player: Player): Boolean {
        val item = itemProvider.getPlayerItem(player, key, ignoreEnchants, ignoreLore) ?: return false
        return provider.has(player, item, amount)
    }

    override fun processRequirement(player: Player): Boolean {
        val item = itemProvider.getPlayerItem(player, key, ignoreEnchants, ignoreLore) ?: return false
        return provider.take(player, item, amount)
    }

    override fun getIcon(): ItemStack {


        // TODO
        /*return provider.getItem(itemName) ?: */
        return ItemStack(Material.DIAMOND)
    }

    override fun getName(): String = "$amount"

    override fun getType(): String =
        MagicBeesAPI.get()
            ?.getTranslationsController()
            ?.getString(
                provider.getName().uppercase(),
                MagicBeesAPI.get()
                    ?.getTranslationsController()
                    ?.getString(
                        if (key.startsWith("!")) key.substring(1) else key
                    ) ?: key
            )?: key
}