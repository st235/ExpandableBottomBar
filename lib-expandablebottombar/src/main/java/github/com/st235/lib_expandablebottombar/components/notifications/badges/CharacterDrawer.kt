package github.com.st235.lib_expandablebottombar.components.notifications.badges

import android.graphics.Paint
import androidx.annotation.Px
import github.com.st235.lib_expandablebottombar.utils.toPx

internal abstract class CharacterDrawer(
    protected val text: String
): BadgeDrawer {

    @Px
    protected open fun getVerticalPadding(): Float {
        return 2F.toPx()
    }

    @Px
    protected open fun getHorizontalPadding(): Float {
        return 2F.toPx()
    }

    @Px
    protected open fun getWidth(paint: Paint): Float {
        return paint.measureText(text, 0, text.length)
    }

    @Px
    protected open fun getHeight(paint: Paint): Float {
        val fm = paint.fontMetrics
        return fm.descent - fm.ascent
    }

}
