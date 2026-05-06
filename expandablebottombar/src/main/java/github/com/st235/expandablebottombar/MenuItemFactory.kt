package github.com.st235.expandablebottombar

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import github.com.st235.expandablebottombar.components.MenuItemView
import github.com.st235.expandablebottombar.utils.DrawableHelper
import github.com.st235.expandablebottombar.utils.StyleController

internal open class MenuItemFactory @Suppress("LongParameterList") constructor(
    private val rootView: ExpandableBottomBar,
    private val styleController: StyleController,
    @Px private val itemVerticalPadding: Int,
    @Px private val itemHorizontalPadding: Int,
    @Px private val backgroundCornerRadius: Float,
    @FloatRange(from = 0.0, to = 1.0) private val backgroundOpacity: Float,
    @ColorInt private val itemInactiveColor: Int,
    @ColorInt private val globalNotificationBadgeColor: Int,
    @ColorInt private val globalNotificationBadgeTextColor: Int,
    @param:Dimension(Dimension.PX) private val textSize: Float? = null,
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
            contentDescription = context.resources.getString(
                R.string.accessibility_item_description,
                menuItemDescriptor.text
            )
            setPadding(
                itemHorizontalPadding,
                itemVerticalPadding,
                itemHorizontalPadding,
                itemVerticalPadding
            )

            setIcon(
                menuItemDescriptor.iconId,
                backgroundColorStateList
            )
            setText(
                text = menuItemDescriptor.text,
                style = MenuItemView.TextStyle(
                    backgroundColor = backgroundColorStateList,
                    size = textSize,
                )
            )
            notificationBadgeBackgroundColor =
                menuItemDescriptor.badgeBackgroundColor ?: globalNotificationBadgeColor
            notificationBadgeTextColor =
                menuItemDescriptor.badgeTextColor ?: globalNotificationBadgeTextColor

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
