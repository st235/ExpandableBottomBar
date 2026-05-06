package github.com.st235.expandablebottombar.components

import android.content.Context
import android.content.res.ColorStateList
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import github.com.st235.expandablebottombar.NotificationBadge
import github.com.st235.expandablebottombar.R
import github.com.st235.expandablebottombar.components.notifications.ExpandableBottomBarNotificationBadgeView
import github.com.st235.expandablebottombar.state.MenuItemSavedState
import github.com.st235.expandablebottombar.utils.DrawableHelper

private const val DEFAULT_NOTIFICATION_TEXT_LENGTH = 4

internal class MenuItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NotificationBadge {

    data class TextStyle(
        val backgroundColor: ColorStateList,
        @param:Dimension(unit = Dimension.PX) val size: Float? = null,
    )

    private val iconView: ExpandableBottomBarNotificationBadgeView
    private val titleView: AppCompatTextView

    override var notificationBadgeBackgroundColor: Int
        get() {
            return iconView.badgeColor
        }
        set(@ColorInt value) {
            iconView.badgeColor = value
        }

    override var notificationBadgeTextColor: Int
        get() {
            return iconView.badgeTextColor
        }
        set(@ColorInt value) {
            iconView.badgeTextColor = value
        }

    init {
        inflate(context, R.layout.content_bottombar_menu_item, this)

        iconView = findViewById(R.id.icon_view)
        titleView = findViewById(R.id.title_view)

        orientation =  HORIZONTAL
        gravity = Gravity.CENTER
        isFocusable = true
        clipToPadding = false
        clipChildren = false
    }

    override fun onSaveInstanceState(): Parcelable {
        return MenuItemSavedState(iconView.getState(), super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is MenuItemSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        iconView.restore(state.badgeState)
    }

    fun setIcon(@DrawableRes drawableRes: Int, backgroundColorSelector: ColorStateList) {
        iconView.setImageDrawable(
            DrawableHelper.createDrawable(
                context,
                drawableRes,
                backgroundColorSelector
            )
        )
    }

    fun setText(text: CharSequence, style: TextStyle) {
        titleView.text = text
        titleView.setTextColor(style.backgroundColor)
        style.size?.let {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
    }

    fun select() {
        titleView.visibility = VISIBLE
        titleView.isSelected = true
        iconView.isSelected = true
        isSelected = true
    }

    fun deselect() {
        titleView.visibility = GONE
        titleView.isSelected = false
        iconView.isSelected = false
        isSelected = false
    }

    override fun showNotification() {
        iconView.showBadge = true
        iconView.badgeText = null
    }

    override fun showNotification(text: String) {
        require(text.length < DEFAULT_NOTIFICATION_TEXT_LENGTH) {
            "Notifications support text no longer than $DEFAULT_NOTIFICATION_TEXT_LENGTH characters only."
        }

        iconView.showBadge = true
        iconView.badgeText = text
    }

    override fun clearNotification() {
        iconView.showBadge = false
        iconView.badgeText = null
    }

}
