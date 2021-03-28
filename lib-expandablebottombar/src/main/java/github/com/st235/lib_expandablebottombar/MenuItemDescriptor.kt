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
data class MenuItemDescriptor(
    @IdRes val itemId: Int,
    @DrawableRes val iconId: Int,
    val text: CharSequence,
    @ColorInt val activeColor: Int,
    @ColorInt val badgeBackgroundColor: Int?,
    @ColorInt val badgeTextColor: Int?
) {
    class BuildRequest internal constructor(
        private val builder: Builder,
        private val context: Context
    ) {

        @IdRes
        private var itemId: Int = 0

        @DrawableRes
        private var iconId: Int = 0
        var text: CharSequence? = null
        var activeColor: Int? = null

        @ColorInt
        var badgeBackgroundColor: Int? = null

        @ColorInt
        var badgeTextColor: Int? = null

        fun id(@IdRes id: Int): BuildRequest {
            this.itemId = id
            return this
        }

        fun icon(@DrawableRes iconId: Int): BuildRequest {
            this.iconId = iconId
            return this
        }

        fun text(text: CharSequence): BuildRequest {
            this.text = text
            return this
        }

        fun textRes(@StringRes textId: Int): BuildRequest {
            this.text = context.getText(textId)
            return this
        }

        fun color(@ColorInt color: Int): BuildRequest {
            this.activeColor = color
            return this
        }

        fun colorRes(@ColorRes colorId: Int): BuildRequest {
            this.activeColor = ContextCompat.getColor(context, colorId)
            return this
        }

        fun badgeBackgroundColor(@ColorInt badgeBackgroundColor: Int): BuildRequest {
            this.badgeBackgroundColor = badgeBackgroundColor
            return this
        }

        fun badgeBackgroundColorRes(@ColorRes badgeBackgroundColorRes: Int): BuildRequest {
            this.badgeBackgroundColor = ContextCompat.getColor(context, badgeBackgroundColorRes)
            return this
        }

        fun badgeTextColor(@ColorInt badgeTextColor: Int): BuildRequest {
            this.badgeTextColor = badgeTextColor
            return this
        }

        fun badgeTextColorRes(@ColorRes badgeTextColorRes: Int): BuildRequest {
            this.badgeTextColor = ContextCompat.getColor(context, badgeTextColorRes)
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
            builder.items.add(
                MenuItemDescriptor(
                    itemId,
                    iconId,
                    text!!,
                    activeColor!!,
                    badgeBackgroundColor,
                    badgeTextColor
                )
            )
            return builder
        }
    }

    /**
     * Class-helper to create expandable bottom bar menu items
     */
    class Builder(private val context: Context) {
        internal val items = mutableListOf<MenuItemDescriptor>()

        fun addItem() = BuildRequest(this, context)

        fun addItem(@IdRes itemId: Int, @DrawableRes iconId: Int) =
            BuildRequest(this, context).id(itemId).icon(iconId)

        fun addItem(
            @IdRes itemId: Int,
            @DrawableRes iconId: Int,
            @StringRes textId: Int,
            @ColorInt activeColor: Int = Color.BLACK
        ) = BuildRequest(this, context).id(itemId).icon(iconId).textRes(textId)
            .color(activeColor).create()

        fun build(): List<MenuItemDescriptor> = items
    }
}
