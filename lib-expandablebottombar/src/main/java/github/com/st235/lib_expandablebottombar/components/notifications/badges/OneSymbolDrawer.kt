package github.com.st235.lib_expandablebottombar.components.notifications.badges

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt
import github.com.st235.lib_expandablebottombar.utils.max

internal class OneSymbolDrawer(
    @ColorInt private val textColor: Int,
    text: String
): CharacterDrawer(text) {

    override fun draw(paint: Paint, viewBounds: RectF, canvas: Canvas) {
        val width = getTextWidth(paint)
        val height = getTextHeight(paint)

        val topRightX = viewBounds.right
        val topRightY = viewBounds.top

        val radius = max(width, height) / 2F + max(getVerticalPadding(), getHorizontalPadding())

        val centerX = topRightX - radius / 2F
        val centerY = topRightY + radius / 2F

        canvas.drawCircle(centerX, centerY, radius, paint)

        val oldColor = paint.color

        paint.color = textColor
        canvas.drawText(text,
            centerX,
            centerY + getTextOffset(paint),
            paint
        )

        paint.color = oldColor
    }

}
