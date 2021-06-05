package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlin.IllegalStateException
import kotlin.jvm.Throws

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
    class Builder {

        constructor(context: Context) {
            this.context = context
        }

        constructor(context: Context, @IdRes itemId: Int, @DrawableRes iconId: Int) {
            this.context = context
            this.itemId = itemId
            this.iconId = iconId
        }

        constructor(
            context: Context,
            @IdRes itemId: Int,
            @DrawableRes iconId: Int,
            @StringRes textId: Int,
            @ColorInt activeColor: Int = Color.BLACK
        ) {
            this.context = context
            this.itemId = itemId
            this.iconId = iconId
            this.text = context.getText(textId)
            this.activeColor = activeColor
        }

        private val context: Context

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

        fun id(@IdRes id: Int): Builder {
            this.itemId = id
            return this
        }

        fun icon(@DrawableRes iconId: Int): Builder {
            this.iconId = iconId
            return this
        }

        fun text(text: CharSequence): Builder {
            this.text = text
            return this
        }

        fun textRes(@StringRes textId: Int): Builder {
            this.text = context.getText(textId)
            return this
        }

        fun color(@ColorInt color: Int): Builder {
            this.activeColor = color
            return this
        }

        fun colorRes(@ColorRes colorId: Int): Builder {
            this.activeColor = ContextCompat.getColor(context, colorId)
            return this
        }

        fun badgeBackgroundColor(@ColorInt badgeBackgroundColor: Int): Builder {
            this.badgeBackgroundColor = badgeBackgroundColor
            return this
        }

        fun badgeBackgroundColorRes(@ColorRes badgeBackgroundColorRes: Int): Builder {
            this.badgeBackgroundColor = ContextCompat.getColor(context, badgeBackgroundColorRes)
            return this
        }

        fun badgeTextColor(@ColorInt badgeTextColor: Int): Builder {
            this.badgeTextColor = badgeTextColor
            return this
        }

        fun badgeTextColorRes(@ColorRes badgeTextColorRes: Int): Builder {
            this.badgeTextColor = ContextCompat.getColor(context, badgeTextColorRes)
            return this
        }

        private fun assertValidity() {
            if (itemId == 0 ||
                iconId == 0 ||
                text == null ||
                activeColor == null
            ) {
                throw IllegalStateException("Menu Item is not constructed properly")
            }
        }

        @Throws(IllegalStateException::class)
        fun build(): MenuItemDescriptor {
            assertValidity()
            return MenuItemDescriptor(
                itemId,
                iconId,
                text!!,
                activeColor!!,
                badgeBackgroundColor,
                badgeTextColor
            )
        }
    }

}
