package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.StyleController

internal open class MenuItemFactory(
    private val rootView: ExpandableBottomBar,
    private val styleController: StyleController,
    @Px private val itemVerticalPadding: Int,
    @Px private var itemHorizontalPadding: Int,
    @Px private var backgroundCornerRadius: Float,
    @FloatRange(from = 0.0, to = 1.0) private var backgroundOpacity: Float,
    @ColorInt private var itemInactiveColor: Int,
    @ColorInt private var globalNotificationBadgeColor: Int,
    @ColorInt private var globalNotificationBadgeTextColor: Int
) {

    fun build(
        menuItemDescriptor: MenuItemDescriptor,
        onItemClickListener: (MenuItem, View) -> Unit
    ): MenuItemImpl {
        val context: Context = rootView.context

        val itemView = createItemView(context = context)
        val menuItem = MenuItemImpl(
            menuItemDescriptor,
            rootView,
            itemView
        )

        val backgroundColorStateList = DrawableHelper.createSelectedUnselectedStateList(
            menuItemDescriptor.activeColor,
            itemInactiveColor
        )

        with(itemView) {
            id = menuItemDescriptor.itemId
            contentDescription = context.resources.getString(R.string.accessibility_item_description, menuItemDescriptor.text)
            setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)

            setIcon(menuItemDescriptor.iconId, backgroundColorStateList)
            setText(menuItemDescriptor.text, backgroundColorStateList)
            notificationBadgeBackgroundColor = menuItemDescriptor.badgeBackgroundColor ?: globalNotificationBadgeColor
            notificationBadgeTextColor = menuItemDescriptor.badgeTextColor ?: globalNotificationBadgeTextColor

            background = createHighlightedMenuShape(menuItemDescriptor)
            setOnClickListener {
                onItemClickListener(menuItem, it)
            }
        }

        return menuItem
    }

    @VisibleForTesting
    internal open fun createItemView(
        context: Context
    ): MenuItemView {
        return MenuItemView(context = context)
    }

    private fun createHighlightedMenuShape(menuItemDescriptor: MenuItemDescriptor): Drawable {
        return styleController.createStateBackground(
            menuItemDescriptor.activeColor,
            backgroundCornerRadius,
            backgroundOpacity
        )
    }

}