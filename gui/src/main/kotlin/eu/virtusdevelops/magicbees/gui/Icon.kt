package eu.virtusdevelops.magicbees.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Icon(
    private var itemStack: ItemStack,
    private val priority: Int = 1,
    private val refreshAction: UpdateAction? = null
) : Comparable<Icon> {
    private val clickActions = mutableListOf<Action>()
    private val leftClickAction = mutableListOf<Action>()
    private val rightClickAction = mutableListOf<Action>()
    private val shiftLeftClickAction = mutableListOf<Action>()
    private val shiftRightClickAction = mutableListOf<Action>()
    private val visiblityCondition : Condition? = null

    fun addClickAction(action: Action) {
        clickActions.add(action)
    }

    fun addLeftClickAction(action: Action) {
        leftClickAction.add(action)
    }
    fun addRightClickAction(action: Action) {
        rightClickAction.add(action)
    }

    fun addShiftLeftClickAction(action: Action) {
        shiftLeftClickAction.add(action)
    }
    fun addShiftRightClickAction(action: Action) {
        shiftRightClickAction.add(action)
    }

    fun setItemStack(itemStack: ItemStack) {
        this.itemStack = itemStack
    }
    fun getItemStack(): ItemStack = itemStack
    fun getPriority(): Int = priority

    fun getRefreshAction(): UpdateAction? = refreshAction

    fun getClickActions(): List<Action> = clickActions
    fun getLeftClickActions(): List<Action> = leftClickAction
    fun getRightClickActions(): List<Action> = rightClickAction
    fun getShiftLeftClickActions(): List<Action> = shiftLeftClickAction
    fun getShiftRightClickActions(): List<Action> = shiftRightClickAction
    fun getVisiblityCondition(): Condition? = visiblityCondition


    fun refresh(player: Player) {
        refreshAction?.update(player, this)
    }

    override fun compareTo(other: Icon): Int {
        return other.getPriority().compareTo(getPriority())
    }
}