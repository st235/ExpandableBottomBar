package github.com.st235.expandablebottombar

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem
import github.com.st235.lib_expandablebottombar.ExpandableItemViewController
import github.com.st235.lib_expandablebottombar.utils.StyleController
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExpandableItemViewControllerTest {

    private val menuItem = ExpandableBottomBarMenuItem(0, 0, "", 0)
    private val styleController = mock<StyleController>()
    private val itemView = mock<View>()
    private val titleView = mock<TextView>()
    private val iconView = mock<ImageView>()
    private val drawable = mock<Drawable>()

    private lateinit var expandableItemViewController: ExpandableItemViewController

    @Before
    fun setUp() {
        expandableItemViewController = object : ExpandableItemViewController(
            menuItem, styleController, itemView, titleView, iconView, 0.0F, 1.0F
        ) {
            override fun createHighlightedMenuShape(): Drawable = drawable
        }
    }

    @Test
    fun `test that select will show title and icon`() {
        expandableItemViewController.select()
        verify(itemView).background = drawable
        verify(titleView).visibility = View.VISIBLE
        verify(titleView).isSelected = true
        verify(iconView).isSelected = true
        verify(itemView).isSelected = true
    }

    @Test
    fun `test that deselect item will hide title and remove highlight`() {
        expandableItemViewController.deselect()
        verify(itemView).background = null
        verify(titleView).visibility = View.GONE
        verify(titleView).isSelected = false
        verify(iconView).isSelected = false
        verify(itemView).isSelected = false
    }
}