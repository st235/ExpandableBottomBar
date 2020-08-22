package github.com.st235.lib_expandablebottombar.utils

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar

internal interface StyleController {

    fun createShapeDrawable(
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float,
        @FloatRange(from = 0.0, to = 1.0) opacity: Float
    ): Drawable

    companion object {

        fun create(style: ExpandableBottomBar.ItemStyle) = when(style) {
            ExpandableBottomBar.ItemStyle.NORMAL -> NormalStyleController()
            ExpandableBottomBar.ItemStyle.OUTLINE -> OutlineStyleController()
            ExpandableBottomBar.ItemStyle.STROKE -> StrokeStyleController()
        }
    }
}

internal class NormalStyleController: StyleController {

    override fun createShapeDrawable(
        color: Int,
        cornerRadius: Float,
        opacity: Float
    ): Drawable {
        return DrawableHelper.createShapeDrawable(
            color = color,
            shouldFill = true,
            shouldStroke = false,
            cornerRadius = cornerRadius,
            opacity = opacity
        )
    }
}

internal class OutlineStyleController: StyleController {

    override fun createShapeDrawable(color: Int, cornerRadius: Float, opacity: Float): Drawable {
        return DrawableHelper.createShapeDrawable(
            color = color,
            shouldFill = false,
            shouldStroke = true,
            cornerRadius = cornerRadius,
            opacity = opacity
        )
    }
}

internal class StrokeStyleController: StyleController {

    override fun createShapeDrawable(color: Int, cornerRadius: Float, opacity: Float): Drawable {
        return DrawableHelper.createShapeDrawable(
            color = color,
            shouldFill = true,
            shouldStroke = true,
            cornerRadius = cornerRadius,
            opacity = opacity
        )
    }
}

