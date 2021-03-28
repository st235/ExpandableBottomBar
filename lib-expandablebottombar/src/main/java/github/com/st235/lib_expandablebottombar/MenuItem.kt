package github.com.st235.lib_expandablebottombar

import androidx.annotation.ColorInt
import androidx.annotation.IdRes

interface MenuItem {

    @get:IdRes
    val id: Int

    val text: CharSequence

    @get:ColorInt
    val activeColor: Int

    val isShown: Boolean

    /**
     * Shows item
     */
    fun show()

    /**
     * Hides item
     *
     * @throws IllegalStateException when item is selected
     */
    @Throws(IllegalStateException::class)
    fun hide()

    fun notification(): Notification

}
