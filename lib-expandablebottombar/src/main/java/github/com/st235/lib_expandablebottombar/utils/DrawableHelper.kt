package github.com.st235.lib_expandablebottombar.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat

internal object DrawableHelper {
    fun createShapeDrawable(@ColorInt color: Int,
                                     shouldFill: Boolean = true,
                                     shouldStroke: Boolean = false,
                                     @FloatRange(from = 0.0) cornerRadius: Float,
                                     @FloatRange(from = 0.0, to = 1.0) opacity: Float): Drawable {
        val footerBackground = GradientDrawable()

        val radii = FloatArray(8)
        for (i in 0 until 8) radii[i] = cornerRadius

        footerBackground.shape = GradientDrawable.RECTANGLE
        footerBackground.cornerRadii = radii

        if (shouldFill) {
            footerBackground.setColor(ColorUtils.setAlphaComponent(color, (opacity * 255).toInt()))
        }

        if (shouldStroke) {
            footerBackground.setStroke(2.toPx(), color)
        }

        return footerBackground
    }

    fun createSelectedUnselectedStateList(
        @ColorInt selectedColor: Int,
        @ColorInt unselectedColor: Int
    ): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            ),
            intArrayOf(selectedColor, unselectedColor)
        )
    }

    fun createDrawable(context: Context,
                                @DrawableRes menuItem: Int,
                                stateList: ColorStateList): Drawable {
        val iconDrawable = DrawableCompat.wrap(
            ContextCompat.getDrawable(context, menuItem).deepCopy()
        )
        DrawableCompat.setTintList(iconDrawable, stateList)
        return iconDrawable
    }

    private fun Drawable?.deepCopy(): Drawable {
        return this?.mutate()?.constantState?.newDrawable()
            ?: throw IllegalStateException("Cannot clone existing drawable")
    }
}
