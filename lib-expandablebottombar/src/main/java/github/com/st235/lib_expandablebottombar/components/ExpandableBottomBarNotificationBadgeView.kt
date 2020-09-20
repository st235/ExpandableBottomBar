package github.com.st235.lib_expandablebottombar.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import github.com.st235.lib_expandablebottombar.state.NotificationBadgeSavedState
import github.com.st235.lib_expandablebottombar.utils.max
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {
            badgeDrawer?.draw(paint, viewBounds, canvas)
        }
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

    interface BadgeDrawer {

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
                    shouldShowBadge && (text?.length == 1) -> OneSymbolDrawer(textColor, text)
                    shouldShowBadge && !text.isNullOrBlank() -> MultipleTextDrawer(textColor, text)
                    else -> NoOpDrawer()
                }
            }

        }

    }

    class NoOpDrawer: BadgeDrawer {

        override fun draw(
            paint: Paint,
            viewBounds: RectF,
            canvas: Canvas
        ) {
            // empty on purpose
        }

    }

    class SimpleDotDrawer: BadgeDrawer {

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

    abstract class CharacterDrawer(
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

    class OneSymbolDrawer(
        @ColorInt private val textColor: Int,
        text: String
    ): CharacterDrawer(text) {

        override fun draw(paint: Paint, viewBounds: RectF, canvas: Canvas) {
            val width = getWidth(paint)
            val height = getHeight(paint)

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
                centerY + height / 4F,
                paint
            )

            paint.color = oldColor
        }

    }

    class MultipleTextDrawer(
        @ColorInt private val textColor: Int,
        text: String
    ) : CharacterDrawer(text) {

        private val buffer = RectF()

        override fun draw(paint: Paint, viewBounds: RectF, canvas: Canvas) {
            val width = getWidth(paint)
            val height = getHeight(paint)

            val topRightX = viewBounds.right
            val topRightY = viewBounds.top

            val radius = max(width, height) / 2F + max(getVerticalPadding(), getHorizontalPadding())

            val centerX = topRightX - radius / 2F
            val centerY = topRightY + radius / 2F

            buffer.set(
                centerX - width / 2F - getHorizontalPadding(), //left
                centerY - height / 2F - getVerticalPadding(), //top
                centerX + width / 2F + getHorizontalPadding(), //right
                centerY + height / 2F + getVerticalPadding() //bottom
            )

            canvas.drawRoundRect(buffer, getCornerRoundRadius(), getCornerRoundRadius(), paint)

            val oldColor = paint.color

            paint.color = textColor
            canvas.drawText(text,
                centerX,
                centerY + height / 4F,
                paint
            )

            paint.color = oldColor
        }

        override fun getHorizontalPadding(): Float {
            return 3F.toPx()
        }

        @Px
        private fun getCornerRoundRadius(): Float {
           return 8F.toPx()
        }

    }
}
