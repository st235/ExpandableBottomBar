package github.com.st235.lib_expandablebottombar.components.notifications

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import github.com.st235.lib_expandablebottombar.components.notifications.badges.BadgeDrawer
import github.com.st235.lib_expandablebottombar.state.NotificationBadgeSavedState
import github.com.st235.lib_expandablebottombar.utils.toPx

internal class ExpandableBottomBarNotificationBadgeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val stateController = NotificationBadgeStateController()

    private val viewBounds = RectF()

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 10F.toPx()
    }

    private var badgeDrawer: BadgeDrawer? = null

    @get:ColorInt
    var badgeColor: Int
    get() = paint.color
    set(@ColorInt value) {
        paint.color = value
        invalidate()
    }

    @ColorInt
    var badgeTextColor: Int = Color.WHITE
    set(value) {
        field = value
        badgeDrawer = BadgeDrawer.newDrawer(badgeText, badgeTextColor, showBadge)
        invalidate()
    }

    var badgeText: String? = null
    set(value) {
        field = value
        badgeDrawer = BadgeDrawer.newDrawer(badgeText, badgeTextColor, showBadge)
        invalidate()
    }

    var showBadge: Boolean = false
    set(value) {
        field = value
        badgeDrawer = BadgeDrawer.newDrawer(badgeText, badgeTextColor, showBadge)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewBounds.set(0F, 0F, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        badgeDrawer?.draw(paint, viewBounds, canvas)
    }

    fun getState() = stateController.store(onSaveInstanceState())

    fun restore(state: NotificationBadgeSavedState) {
        super.onRestoreInstanceState(state.superState)
        stateController.restore(state)
    }

    inner class NotificationBadgeStateController {

        fun store(superState: Parcelable?) =
            NotificationBadgeSavedState(
                badgeColor,
                badgeTextColor,
                badgeText,
                showBadge,
                superState
            )

        fun restore(savedState: NotificationBadgeSavedState) {
            showBadge = savedState.shouldShowBadge
            badgeColor = savedState.badgeColor
            badgeTextColor = savedState.badgeTextColor
            badgeText = savedState.badgeText
        }

    }
}
