package github.com.st235.lib_expandablebottombar

import androidx.annotation.ColorInt

internal interface ExpandableBottomBarNotificationBadge {

    fun showNotification()

    @Throws(IllegalArgumentException::class)
    fun showNotification(text: String)

    fun clearNotification()

    fun setNotificationBadgeBackground(@ColorInt color: Int)

    fun setNotificationBadgeTextColor(@ColorInt color: Int)

}
