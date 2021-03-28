package github.com.st235.lib_expandablebottombar

import androidx.annotation.ColorInt
import java.lang.ref.WeakReference

class Notification internal constructor(
        badge: NotificationBadge
) {

    private val viewRef = WeakReference(badge)

    private val view: NotificationBadge?
    get() {
        return viewRef.get()
    }

    /**
     * background color of a badge
     *
     * * @throws IllegalStateException when view is destroyed
     */
    @get:ColorInt
    @get:Throws(IllegalStateException::class)
    var badgeColor: Int
    set(@ColorInt value: Int) {
        view?.notificationBadgeBackgroundColor = value
    }
    get() {
        val view = view ?: throw IllegalStateException("View is not longer active")
        return view.notificationBadgeBackgroundColor
    }

    /**
     * text color of a badge
     *
     * @throws IllegalStateException when view is destroyed
     */
    @get:ColorInt
    @get:Throws(IllegalStateException::class)
    var badgeTextColor: Int
        set(@ColorInt value: Int) {
            view?.notificationBadgeTextColor = value
        }
        get() {
            val view = view ?: throw IllegalStateException("View is not longer active")
            return view.notificationBadgeTextColor
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
