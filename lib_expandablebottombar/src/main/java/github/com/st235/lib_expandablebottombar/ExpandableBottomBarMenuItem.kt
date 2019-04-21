package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes

/**
 * Menu item for expandable bottom bar
 */
data class ExpandableBottomBarMenuItem(
    @IdRes val itemId: Int,
    @DrawableRes val iconId: Int,
    val text: CharSequence,
    @ColorInt val activeColor: Int
) {
    /**
     * Class-helper to create expandable bottom bar menu items
     */
    class Builder(private val context: Context) {
        private val items = mutableListOf<ExpandableBottomBarMenuItem>()

        fun addItem(
            @IdRes itemId: Int,
            @DrawableRes iconId: Int,
            @StringRes textId: Int,
            @ColorInt activeColor: Int = Color.BLACK
        ): Builder {
            val text = context.getText(textId)
            items.add(ExpandableBottomBarMenuItem(itemId, iconId, text, activeColor))
            return this
        }

        fun addItem(
            @IdRes itemId: Int,
            @DrawableRes iconId: Int,
            text: CharSequence,
            @ColorInt activeColor: Int = Color.BLACK
        ): Builder {
            items.add(ExpandableBottomBarMenuItem(itemId, iconId, text, activeColor))
            return this
        }

        fun build(): List<ExpandableBottomBarMenuItem> = items
    }
}