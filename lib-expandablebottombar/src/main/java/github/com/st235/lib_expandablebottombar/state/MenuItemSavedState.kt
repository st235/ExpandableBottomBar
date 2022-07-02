package github.com.st235.lib_expandablebottombar.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class MenuItemSavedState(
    val badgeState: NotificationBadgeSavedState,
    val superState: Parcelable?
): Parcelable
