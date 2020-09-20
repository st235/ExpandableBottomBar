package github.com.st235.lib_expandablebottombar

import androidx.annotation.ColorInt
import github.com.st235.lib_expandablebottombar.components.ExpandableBottomBarNotificationBadgeView
import java.lang.ref.WeakReference

class ExpandableBottomBarNotification internal constructor(
    notificationView: ExpandableBottomBarNotificationBadgeView
) {

    private val viewRef = WeakReference(notificationView)

    private val view: ExpandableBottomBarNotificationBadgeView?
    get() {
        return viewRef.get()
    }

    fun setBadgeColor(@ColorInt color: Int) {
        view?.badgeColor = color
    }

    fun show() {
        view?.showBadge = true
        view?.badgeText = null
    }

    fun show(text: String) {
        if (text.length > 4) {
            throw IllegalArgumentException("Text is longer than 4 symbols")
        }

        view?.showBadge = true
        view?.badgeText = text
    }

    fun clear() {
        view?.showBadge = false
        view?.badgeText = null
    }
}
