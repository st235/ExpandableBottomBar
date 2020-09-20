package github.com.st235.expandablebottombar

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem
import github.com.st235.lib_expandablebottombar.ExpandableItemViewController
import github.com.st235.lib_expandablebottombar.components.ExpandableBottomBarMenuItemView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExpandableItemViewControllerTest {

    private val menuItem = ExpandableBottomBarMenuItem(0, 0, "", 0, null, null)
    private val itemView = mock<ExpandableBottomBarMenuItemView>()

    private lateinit var expandableItemViewController: ExpandableItemViewController

    @Before
    fun setUp() {
        expandableItemViewController = ExpandableItemViewController(menuItem, itemView)
    }

    @Test
    fun `test that select will show title and icon`() {
        expandableItemViewController.select()
        verify(itemView).select()
    }

    @Test
    fun `test that deselect item will hide title and remove highlight`() {
        expandableItemViewController.unselect()
        verify(itemView).deselect()
    }
}