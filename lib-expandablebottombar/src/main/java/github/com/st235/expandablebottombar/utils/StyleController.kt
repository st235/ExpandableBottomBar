package github.com.st235.lib_expandablebottombar.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar

internal abstract class StyleController {

    fun createStateBackground(
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float,
        @FloatRange(from = 0.0, to = 1.0) opacity: Float
    ): Drawable {
        val stateDrawable = StateListDrawable()

        stateDrawable.addState(
            intArrayOf(android.R.attr.state_selected),
            createSelectedStateBackground(color, cornerRadius, opacity)
        )

        return stateDrawable
    }

    protected abstract fun createSelectedStateBackground(
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

internal class NormalStyleController: StyleController() {

    override fun createSelectedStateBackground(
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

internal class OutlineStyleController: StyleController() {

    override fun createSelectedStateBackground(color: Int, cornerRadius: Float, opacity: Float): Drawable {
        return DrawableHelper.createShapeDrawable(
            color = color,
            shouldFill = false,
            shouldStroke = true,
            cornerRadius = cornerRadius,
            opacity = opacity
        )
    }
}

internal class StrokeStyleController: StyleController() {

    override fun createSelectedStateBackground(color: Int, cornerRadius: Float, opacity: Float): Drawable {
        return DrawableHelper.createShapeDrawable(
            color = color,
            shouldFill = true,
            shouldStroke = true,
            cornerRadius = cornerRadius,
            opacity = opacity
        )
    }
}

