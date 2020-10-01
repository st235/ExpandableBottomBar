package github.com.st235.lib_expandablebottombar.components.notifications.badges

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.Px
import github.com.st235.lib_expandablebottombar.utils.toPx

internal class SimpleDotDrawer: BadgeDrawer {

    override fun draw(
        paint: Paint,
        viewBounds: RectF,
        canvas: Canvas
    ) {
        val radius = getRadius()

        val topRightX = viewBounds.right
        val topRightY = viewBounds.top

        canvas.drawCircle(topRightX - radius / 2F, topRightY + radius / 2F, radius, paint)
    }

    @Px
    private fun getRadius(): Float {
        return 4F.toPx()
    }
}