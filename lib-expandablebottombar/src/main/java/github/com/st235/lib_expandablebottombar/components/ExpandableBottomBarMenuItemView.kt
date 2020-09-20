package github.com.st235.lib_expandablebottombar.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarNotificationBadge
import github.com.st235.lib_expandablebottombar.R
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import kotlinx.android.synthetic.main.content_bottombar_menu_item.view.*

internal class ExpandableBottomBarMenuItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ExpandableBottomBarNotificationBadge {

    init {
        inflate(context, R.layout.content_bottombar_menu_item, this)

        orientation =  HORIZONTAL
        gravity = Gravity.CENTER
        isFocusable = true
        clipToPadding = false
        clipChildren = false
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

    fun setText(text: CharSequence, textColorSelector: ColorStateList) {
        titleView.text = text
        titleView.setTextColor(textColorSelector)
    }

    fun select() {
        titleView.visibility = View.VISIBLE
        titleView.isSelected = true
        iconView.isSelected = true
        isSelected = true
    }

    fun deselect() {
        titleView.visibility = View.GONE
        titleView.isSelected = false
        iconView.isSelected = false
        isSelected = false
    }

    override fun showNotification() {
        iconView.showBadge = true
        iconView.badgeText = null
    }

    override fun showNotification(text: String) {
        if (text.length > 4) {
            throw IllegalArgumentException("Text is longer than 4 symbols, which is not acceptable")
        }

        iconView.showBadge = true
        iconView.badgeText = text
    }

    override fun clearNotification() {
        iconView.showBadge = false
        iconView.badgeText = null
    }

    override fun setNotifiationBadgeBackground(color: Int) {
        iconView.badgeColor = color
    }

    override fun setNotifiationBadgeTextColor(color: Int) {
        iconView.badgeTextColor = color
    }
}