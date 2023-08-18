package github.com.st235.expandablebottombar

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.Menu
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import github.com.st235.lib_expandablebottombar.MenuItemImpl
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import github.com.st235.lib_expandablebottombar.utils.ConstraintLayoutHelper
import github.com.st235.lib_expandablebottombar.utils.TransitionHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class MenuItemImplTest {

    private val transitionHelper = mock<TransitionHelper>()
    private val menuItemDescriptor = mock<MenuItemDescriptor>()
    private val rootView = mock<ExpandableBottomBar>()
    private val itemView = mock<MenuItemView>()
    private val menu = mock<Menu>()

    private val layoutParams = mock<ConstraintLayout.LayoutParams>()
    private val constraintHelper = mock<ConstraintLayoutHelper>()

    private val menuItemImpl = MenuItemImpl(
        menuItemDescriptor,
        rootView,
        itemView,
        transitionHelper,
        constraintHelper
    )

    @Before
    fun setUp() {
        whenever(rootView.menu).thenReturn(menu)
        whenever(constraintHelper.layoutParams(any(), any())).thenReturn(layoutParams)
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

    @Test
    fun `test that isShown returns visible when view is visible`() {
        whenever(itemView.visibility).thenReturn(View.VISIBLE)
        assertTrue(menuItemImpl.isShown)
        verify(itemView, times(1)).visibility
    }

    @Test
    fun `test that isShown returns invisible when view is invisible or gone`() {
        whenever(itemView.visibility).thenReturn(View.INVISIBLE)
        assertFalse(menuItemImpl.isShown)

        whenever(itemView.visibility).thenReturn(View.GONE)
        assertFalse(menuItemImpl.isShown)

        verify(itemView, times(2)).visibility
    }

    @Test
    fun `test that show set delayedTransaction and shows itemView`() {
        menuItemImpl.show()

        verify(transitionHelper, times(1)).apply(rootView, -1L)
        verify(itemView, times(1)).visibility = View.VISIBLE
    }

    @Test
    fun `test that hide will call hide on itemView, set delayed transaction and will not deselect item`() {
        menuItemImpl.hide()

        verify(transitionHelper, times(1)).apply(rootView, -1L)
        verify(itemView, times(1)).visibility = View.GONE
        verify(menu, never()).deselect()
    }

    @Test
    fun `test that hide will call hide on itemview, delayed transaction and will deselect active item`() {
        whenever(menu.selectedItem).thenReturn(menuItemImpl)

        menuItemImpl.hide()

        verify(transitionHelper, times(1)).apply(rootView, -1L)
        verify(itemView, times(1)).visibility = View.GONE
        verify(menu, times(1)).deselect()
    }

    @Test
    fun `test that getView will return itemView`() {
        assertEquals(itemView, menuItemImpl.getView())
    }

    @Test
    fun `test that attach to superview calls addView on parent and changes status`() {
        menuItemImpl.attachToSuperView(0, 0)

        verify(constraintHelper, times(1)).layoutParams(0, 0)
        verify(rootView, times(1)).addView(itemView, layoutParams)
        assertTrue(menuItemImpl.isAttached)
    }

    @Test
    fun `test that remove from superview calls removeView on parent and changes status`() {
        menuItemImpl.removeFromSuperView()

        verify(rootView, times(1)).removeView(itemView)
        assertFalse(menuItemImpl.isAttached)
    }

    @Test
    fun `test that rebuildSiblingsConnection will create new constraint set and apply it to the rootView`() {
        menuItemImpl.rebuildSiblingsConnection(-1, -1)

        verify(constraintHelper, times(1)).applyNewConstraintSetFor(itemView, rootView, -1, -1)
    }

}
