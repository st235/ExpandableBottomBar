package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.lang.IllegalStateException

/**
 * Menu item for expandable bottom bar
 */
data class ExpandableBottomBarMenuItem(
    @IdRes val itemId: Int,
    @DrawableRes val iconId: Int,
    val text: CharSequence,
    @ColorInt val activeColor: Int
) {
    class ItemBuildRequest internal constructor(private val builder: Builder, private val context: Context) {

        @IdRes
        private var itemId: Int = 0
        @DrawableRes
        private var iconId: Int = 0
        var text: CharSequence? = null
        var activeColor: Int? = null

        fun id(@IdRes id: Int): ItemBuildRequest {
            this.itemId = id
            return this
        }

        fun icon(@DrawableRes iconId: Int): ItemBuildRequest {
            this.iconId = iconId
            return this
        }

        fun text(text: CharSequence): ItemBuildRequest {
            this.text = text
            return this
        }

        fun textRes(@StringRes textId: Int): ItemBuildRequest {
            this.text = context.getText(textId)
            return this
        }

        fun color(@ColorInt color: Int): ItemBuildRequest {
            this.activeColor = color
            return this
        }

        fun colorRes(@ColorRes colorId: Int): ItemBuildRequest {
            this.activeColor = ContextCompat.getColor(context, colorId)
            return this
        }

        private fun assertValidity() {
            if (itemId == 0 ||
                iconId == 0 ||
                text == null ||
                activeColor == null
            ) {
                throw IllegalStateException("Menu Item not constructed properly")
            }
        }

        fun create(): Builder {
            assertValidity()
            builder.items.add(ExpandableBottomBarMenuItem(itemId, iconId, text!!, activeColor!!))
            return builder
        }
    }

    /**
     * Class-helper to create expandable bottom bar menu items
     */
    class Builder(private val context: Context) {
        internal val items = mutableListOf<ExpandableBottomBarMenuItem>()

        fun addItem() = ItemBuildRequest(this, context)

        fun addItem(@IdRes itemId: Int, @DrawableRes iconId: Int) =
            ItemBuildRequest(this, context).id(itemId).icon(iconId)

        fun addItem(
            @IdRes itemId: Int,
            @DrawableRes iconId: Int,
            @StringRes textId: Int,
            @ColorInt activeColor: Int = Color.BLACK
        ) = ItemBuildRequest(this, context).id(itemId).icon(iconId).textRes(textId).color(activeColor).create()

        fun build(): List<ExpandableBottomBarMenuItem> = items
    }
}
