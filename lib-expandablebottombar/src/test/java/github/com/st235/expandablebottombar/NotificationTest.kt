package github.com.st235.expandablebottombar

import android.graphics.Color
import github.com.st235.lib_expandablebottombar.Notification
import github.com.st235.lib_expandablebottombar.NotificationBadge
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class NotificationTest {

    private val badge = mock<NotificationBadge>()

    private val notification = Notification(badge)

    @Test
    fun `test that badge color will be taken from the badge`() {
        whenever(badge.notificationBadgeBackgroundColor).thenReturn(Color.WHITE)

        assertEquals(Color.WHITE, notification.badgeColor)
    }

    @Test
    fun `test that set badge color will be taken from the badge`() {
        notification.badgeColor = Color.WHITE

        verify(badge, times(1)).notificationBadgeBackgroundColor = Color.WHITE
    }

    @Test
    fun `test that badge text color will be taken from the badge`() {
        whenever(badge.notificationBadgeTextColor).thenReturn(Color.WHITE)

        assertEquals(Color.WHITE, notification.badgeTextColor)
    }

    @Test
    fun `test that set text badge color will be taken from the badge`() {
        notification.badgeTextColor = Color.WHITE

        verify(badge, times(1)).notificationBadgeTextColor = Color.WHITE
    }

    @Test
    fun `test that show will call badge showNotification`() {
        notification.show()
        verify(badge, times(1)).showNotification()
    }

    @Test
    fun `test that show with text will call badge showNotification with text`() {
        val text = "Dr.Acula"
        notification.show(text)
        verify(badge, times(1)).showNotification(text)
    }

    @Test
    fun `test that clear will call badge clearNotification`() {
        notification.clear()
        verify(badge, times(1)).clearNotification()
    }

}