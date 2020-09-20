package github.com.st235.lib_expandablebottombar

import androidx.annotation.ColorInt
import java.lang.ref.WeakReference

class ExpandableBottomBarNotification internal constructor(
    notificationView: ExpandableBottomBarNotificationBadge
) {

    private val viewRef = WeakReference(notificationView)

    private val view: ExpandableBottomBarNotificationBadge?
    get() {
        return viewRef.get()
    }

    fun setBadgeColor(@ColorInt color: Int) {
        view?.setNotifiationBadgeBackground(color)
    }

    fun setBadgeTextColor(@ColorInt color: Int) {
        view?.setNotifiationBadgeTextColor(color)
    }

    fun show() {
        view?.showNotification()
    }

    fun show(text: String) {
        view?.showNotification(text)
    }

    fun clear() {
        view?.clearNotification()
    }
}
