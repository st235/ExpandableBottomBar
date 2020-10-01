package github.com.st235.lib_expandablebottombar.components.notifications.badges

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

internal class NoOpDrawer: BadgeDrawer {

    override fun draw(
        paint: Paint,
        viewBounds: RectF,
        canvas: Canvas
    ) {
        // empty on purpose
    }

}
