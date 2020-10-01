package github.com.st235.lib_expandablebottombar.components.notifications.badges

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt

internal interface BadgeDrawer {

    fun draw(
        paint: Paint,
        viewBounds: RectF,
        canvas: Canvas
    )

    companion object {

        fun newDrawer(
            text: String?,
            @ColorInt textColor: Int,
            shouldShowBadge: Boolean
        ): BadgeDrawer {
            return when {
                shouldShowBadge && text.isNullOrBlank() -> SimpleDotDrawer()
                shouldShowBadge && (text?.length == 1) -> OneSymbolDrawer(
                    textColor,
                    text
                )
                shouldShowBadge && !text.isNullOrBlank() -> MultipleTextDrawer(
                    textColor,
                    text
                )
                else -> NoOpDrawer()
            }
        }

    }

}
