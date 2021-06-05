package github.com.st235.expandablebottombar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorStateListDrawable
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.anyOrNull
import github.com.st235.lib_expandablebottombar.test.R
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import github.com.st235.lib_expandablebottombar.MenuItemFactory
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.StyleController
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.Field
import java.lang.reflect.Modifier

@RunWith(RobolectricTestRunner::class)
class MenuItemFactoryTest {

    private val itemView = mock<MenuItemView>()
    private val rootView = mock<ExpandableBottomBar>()
    private val styleController = mock<StyleController>()
    private val onItemClick = mock<(MenuItem, View) -> Unit>()
    private val colorStateList = mock<ColorStateList>()
    private val drawableHelper = mockObject(DrawableHelper::class.java)

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
        doAnswer{ colorStateList }.whenever(drawableHelper).createSelectedUnselectedStateList(anyInt(), anyInt())
    }

    @Test
    fun `test that build without notification info will build item menu with global notification info`() {
        menuItemFactory.build(menuItemDescriptorWithoutNotificationInfo, onItemClick)

        verify(itemView, times(1)).id = menuItemDescriptorWithoutNotificationInfo.itemId
        verify(itemView, times(1)).setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)
        verify(itemView, times(1)).setIcon(menuItemDescriptorWithoutNotificationInfo.iconId, colorStateList)
        verify(itemView, times(1)).setText(menuItemDescriptorWithoutNotificationInfo.text, colorStateList)
        verify(itemView, times(1)).notificationBadgeBackgroundColor = globalNotificationBadgeColor
        verify(itemView, times(1)).notificationBadgeTextColor = globalNotificationBadgeTextColor
    }

    @Test
    fun `test that build with notification info will build item menu with local info`() {
        menuItemFactory.build(menuItemDescriptorWithNotificationInfo, onItemClick)

        verify(itemView, times(1)).id = menuItemDescriptorWithNotificationInfo.itemId
        verify(itemView, times(1)).setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)
        verify(itemView, times(1)).setIcon(menuItemDescriptorWithNotificationInfo.iconId, colorStateList)
        verify(itemView, times(1)).setText(menuItemDescriptorWithNotificationInfo.text, colorStateList)
        verify(itemView, times(1)).notificationBadgeBackgroundColor = menuItemDescriptorWithNotificationInfo.badgeBackgroundColor!!
        verify(itemView, times(1)).notificationBadgeTextColor = menuItemDescriptorWithNotificationInfo.badgeTextColor!!
    }

    private fun <T> mockObject(clazz: Class<T>): T {
        val constructor = clazz.declaredConstructors.find { it.parameterCount == 0 }
            ?: throw InstantiationException("class ${clazz.canonicalName} has no empty constructor, " +
                    "is it really a Kotlin \"object\"?")

        constructor.isAccessible = true

        val mockedInstance = spy(constructor.newInstance() as T)

        return replaceObjectInstance(clazz, mockedInstance)
    }

    private fun <T> replaceObjectInstance(clazz: Class<T>, newInstance: T): T {

        if (!clazz.declaredFields.any {
                it.name == "INSTANCE" && it.type == clazz && Modifier.isStatic(it.modifiers)
            }) {
            throw InstantiationException("clazz ${clazz.canonicalName} does not have a static  " +
                    "INSTANCE field, is it really a Kotlin \"object\"?")
        }

        val instanceField = clazz.getDeclaredField("INSTANCE")
        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(instanceField, instanceField.modifiers and Modifier.FINAL.inv())

        instanceField.isAccessible = true
        val originalInstance = instanceField.get(null) as T
        instanceField.set(null, newInstance)
        return newInstance
    }
}