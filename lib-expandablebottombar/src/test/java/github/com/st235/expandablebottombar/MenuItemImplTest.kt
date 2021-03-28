package github.com.st235.expandablebottombar

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import github.com.st235.lib_expandablebottombar.MenuItemImpl
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MenuItemImplTest {

    private val menuItem = MenuItemDescriptor(0, 0, "", 0, null, null)
    private val rootView = mock<ExpandableBottomBar>()
    private val itemView = mock<MenuItemView>()

    private lateinit var menuItemImpl: MenuItemImpl

    @Before
    fun setUp() {
        menuItemImpl = MenuItemImpl(menuItem, rootView, itemView)
    }

    @Test
    fun `test that select will show title and icon`() {
        menuItemImpl.select()
        verify(itemView).select()
    }

    @Test
    fun `test that deselect item will hide title and remove highlight`() {
        menuItemImpl.deselect()
        verify(itemView).deselect()
    }
}
