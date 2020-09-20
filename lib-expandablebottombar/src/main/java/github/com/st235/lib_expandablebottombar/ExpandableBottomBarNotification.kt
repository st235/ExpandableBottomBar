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

    /**
     * @param color - background color of a badge
     */
    fun setBadgeColor(@ColorInt color: Int) {
        view?.setNotifiationBadgeBackground(color)
    }

    /**
     * @param color - text color of a badge
     */
    fun setBadgeTextColor(@ColorInt color: Int) {
        view?.setNotifiationBadgeTextColor(color)
    }

    /**
     * Shows small dot-notification
     */
    fun show() {
        view?.showNotification()
    }

    /**
     * Shows notification badge
     * with passed text
     *
     * Text should be no longer than 4 characters
     *
     * @throws IllegalArgumentException when text is longer than expected
     */
    fun show(text: String) {
        view?.showNotification(text)
    }

    /**
     * Removes notification badge
     */
    fun clear() {
        view?.clearNotification()
    }
}
