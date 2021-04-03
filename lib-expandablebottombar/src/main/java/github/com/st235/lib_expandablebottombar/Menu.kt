package github.com.st235.lib_expandablebottombar

import android.view.View
import androidx.annotation.IdRes
import kotlin.jvm.Throws

typealias OnItemClickListener = (v: View, menuItem: MenuItem, byUser: Boolean) -> Unit

interface Menu: Iterable<MenuItem> {

    var onItemSelectedListener: OnItemClickListener?

    var onItemReselectedListener: OnItemClickListener?

    /**
     * Returns currently selected item
     */
    val selectedItem: MenuItem?

    /**
     * Returns all menu items
     */
    val items: List<MenuItem>

    /**
     * Adds passed item to widget
     *
     * @param descriptor - bottom bar menu item descriptor
     */
    fun add(descriptor: MenuItemDescriptor)

    /**
     * Programmatically select item
     *
     * @param id - identifier of menu item, which should be selected
     */
    @Throws(IllegalArgumentException::class)
    fun select(@IdRes id: Int)

    fun deselect()

    @Throws(IllegalArgumentException::class)
    fun remove(@IdRes id: Int)

    fun removeAll()

    /**
     * Returns menu item for the given id value
     *
     * @throws NullPointerException when id doesn't exists
     */
    @Throws(IllegalArgumentException::class)
    fun findItemById(@IdRes id: Int): MenuItem

}