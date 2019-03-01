package github.com.st235.lib_expandablebottombar.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.graphics.drawable.DrawableCompat

object DrawableHelper {
    internal fun createShapeDrawable(@ColorInt activeColor: Int,
                                     cornerRadius: Float,
                                     opacity: Float): Drawable {
        val footerBackground = ShapeDrawable()

        val radii = FloatArray(8)
        for (i in 0 until 8) radii[i] = cornerRadius

        footerBackground.shape = RoundRectShape(radii, null, null)
        footerBackground.paint.color = ColorUtils.setAlphaComponent(activeColor, (opacity * 255).toInt())

        return footerBackground
    }

    internal fun createDrawable(context: Context,
                                @DrawableRes menuItem: Int,
                                stateList: ColorStateList): Drawable {
        val iconDrawable = ContextCompat.getDrawable(context, menuItem)!!
        DrawableCompat.setTintList(iconDrawable, stateList)
        return iconDrawable
    }
}
