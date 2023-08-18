package github.com.st235.expandablebottombar

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import github.com.st235.lib_expandablebottombar.MenuItemFactory
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import github.com.st235.lib_expandablebottombar.test.R
import github.com.st235.lib_expandablebottombar.utils.StyleController
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MenuItemFactoryTest {

    private val itemView = mock<MenuItemView>()
    private val rootView = mock<ExpandableBottomBar>()
    private val styleController = mock<StyleController>()
    private val onItemClick = mock<(MenuItem, View) -> Unit>()

    private val itemVerticalPadding: Int = 54
    private val itemHorizontalPadding: Int = 61
    private val backgroundCornerRadius: Float = 4.0F
    private val backgroundOpacity: Float = 0.5F
    private val itemInactiveColor: Int = 1
    private val globalNotificationBadgeColor: Int = 2
    private val globalNotificationBadgeTextColor: Int = 3

    private val menuItemDescriptorWithoutNotificationInfo = MenuItemDescriptor(
        R.id.item_id,
        R.id.icon_id,
        "Hello world",
        R.color.active_color,
        null,
        null
    )

    private val menuItemDescriptorWithNotificationInfo = MenuItemDescriptor(
        R.id.item_id,
        R.id.icon_id,
        "Hello world",
        R.color.active_color,
        R.color.notification_color,
        R.color.notification_text_color
    )

    private val menuItemFactory =
        object : MenuItemFactory(
            rootView,
            styleController,
            itemVerticalPadding,
            itemHorizontalPadding,
            backgroundCornerRadius,
            backgroundOpacity,
            itemInactiveColor,
            globalNotificationBadgeColor,
            globalNotificationBadgeTextColor
        ) {
            override fun createItemView(context: Context): MenuItemView {
                return itemView
            }
        }

    @Before
    fun setUp() {
        whenever(rootView.context).thenReturn(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun `test that build without notification info will build item menu with global notification info`() {
        menuItemFactory.build(menuItemDescriptorWithoutNotificationInfo, onItemClick)

        verify(itemView, times(1)).id = menuItemDescriptorWithoutNotificationInfo.itemId
        verify(itemView, times(1)).setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)
        verify(itemView, times(1)).setIcon(eq(menuItemDescriptorWithoutNotificationInfo.iconId), anyOrNull())
        verify(itemView, times(1)).setText(eq(menuItemDescriptorWithoutNotificationInfo.text), anyOrNull())
        verify(itemView, times(1)).notificationBadgeBackgroundColor = globalNotificationBadgeColor
        verify(itemView, times(1)).notificationBadgeTextColor = globalNotificationBadgeTextColor
    }

    @Test
    fun `test that build with notification info will build item menu with local info`() {
        menuItemFactory.build(menuItemDescriptorWithNotificationInfo, onItemClick)

        verify(itemView, times(1)).id = menuItemDescriptorWithNotificationInfo.itemId
        verify(itemView, times(1)).setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)
        verify(itemView, times(1)).setIcon(eq(menuItemDescriptorWithNotificationInfo.iconId), anyOrNull())
        verify(itemView, times(1)).setText(eq(menuItemDescriptorWithNotificationInfo.text), anyOrNull())
        verify(itemView, times(1)).notificationBadgeBackgroundColor = menuItemDescriptorWithNotificationInfo.badgeBackgroundColor!!
        verify(itemView, times(1)).notificationBadgeTextColor = menuItemDescriptorWithNotificationInfo.badgeTextColor!!
    }
}