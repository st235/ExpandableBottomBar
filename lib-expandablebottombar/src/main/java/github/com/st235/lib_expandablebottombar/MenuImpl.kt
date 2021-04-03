package github.com.st235.lib_expandablebottombar

import android.view.View
import androidx.annotation.IdRes
import github.com.st235.lib_expandablebottombar.utils.delayTransition
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

internal class MenuImpl(
    private val rootView: ExpandableBottomBar,
    private val itemFactory: MenuItemFactory,
    private val menuItemHorizontalMargin: Int,
    private val menuItemVerticalMargin: Int,
    private val transitionDuration: Long
): Menu {

    private companion object {
        const val ITEM_NOT_SELECTED = -1
    }

    @IdRes
    private var selectedItemId: Int = ITEM_NOT_SELECTED

    private val itemsLookup = LinkedHashMap<Int, MenuItemImpl>()

    override val selectedItem: MenuItem?
    get() {
        if (selectedItemId == ITEM_NOT_SELECTED) {
            return null
        }

        return itemsLookup.getValue(selectedItemId)
    }

    override val items: List<MenuItem>
    get() {
        return ArrayList(itemsLookup.values)
    }

    override var onItemSelectedListener: OnItemClickListener? = null

    override var onItemReselectedListener: OnItemClickListener? = null

    override fun iterator(): Iterator<MenuItem> {
        return items.iterator()
    }

    override fun add(descriptor: MenuItemDescriptor) {
        rootView.delayTransition(duration = transitionDuration)

        if (selectedItemId == ITEM_NOT_SELECTED) {
            selectedItemId = descriptor.itemId
        }

        val item = createItem(descriptor)
        itemsLookup[descriptor.itemId] = item

        item.attachToSuperView(menuItemHorizontalMargin, menuItemVerticalMargin)
        reconnectConstraints()
        updateAccessibility()
    }

    private fun reconnectConstraints() {
        val itemsList = items

        for ((index, item) in itemsList.withIndex()) {
            val prevIconId =
                if (index - 1 < 0) itemsList.first().id else itemsList[index - 1].id
            val nextIconId =
                if (index + 1 >= itemsList.size) itemsList.last().id else itemsList[index + 1].id

            (item as? MenuItemImpl)?.rebuildSiblingsConnection(prevIconId, nextIconId)
        }
    }

    override fun select(@IdRes id: Int) {
        if (!itemsLookup.containsKey(id)) {
            throw IllegalArgumentException("Cannot select item with id $id because it was not found in the menu")
        }

        selectItemInternal(itemsLookup.getValue(id))
        val selectedItem = selectedItem!! as MenuItemImpl
        onItemSelectedListener?.invoke(selectedItem.getView(), selectedItem, false)
    }

    override fun deselect() {
        if (itemsLookup.isEmpty()) {
            return
        }

        val firstMenuItem = items.find { it.isAttached && it.isShown } as MenuItemImpl
        selectItemInternal(firstMenuItem)
        onItemSelectedListener?.invoke(firstMenuItem.getView(), firstMenuItem, false)
    }

    override fun remove(@IdRes id: Int) {
        if (!itemsLookup.containsKey(id)) {
            throw IllegalArgumentException("Cannot remove item with id $id because it was not found in the menu")
        }

        rootView.delayTransition(duration = transitionDuration)

        val menuItemToRemove = itemsLookup.getValue(id)
        menuItemToRemove.removeFromSuperView()
        itemsLookup.remove(id)
        reconnectConstraints()

        if (selectedItemId == id) {
            deselect()
        }
    }

    override fun removeAll() {
        itemsLookup.clear()
    }

    override fun findItemById(@IdRes id: Int): MenuItem {
        if (!itemsLookup.containsKey(id)) {
            throw IllegalArgumentException("Cannot find item with id $id")
        }

        return itemsLookup.getValue(id)
    }

    private fun selectItemInternal(activeMenuItem: MenuItem) {
        if (selectedItemId == activeMenuItem.id) {
            return
        }

        rootView.delayTransition(duration = transitionDuration)

        itemsLookup.getValue(activeMenuItem.id).select()
        // can be removed from the menu, that's why can be nullable
        itemsLookup[selectedItemId]?.deselect()
        selectedItemId = activeMenuItem.id
    }

    private fun createItem(menuItemDescriptor: MenuItemDescriptor): MenuItemImpl {
        val menuItem = itemFactory.build(menuItemDescriptor) { menuItem: MenuItem, v: View ->
            if (!v.isSelected) {
                selectItemInternal(menuItem)
                onItemSelectedListener?.invoke(v, menuItem, true)
            } else {
                onItemReselectedListener?.invoke(v, menuItem, true)
            }
        }

        if (selectedItemId == menuItemDescriptor.itemId) {
            menuItem.select()
        }

        return menuItem
    }

    private fun updateAccessibility() {
        val items = items
        for ((i, item) in items.withIndex()) {
            val prev = itemsLookup[items.getOrNull(i - 1)?.id]
            val next = itemsLookup[items.getOrNull(i + 1)?.id]

            itemsLookup[item.id]?.setAccessibleWith(prev = prev, next = next)
        }
    }
}