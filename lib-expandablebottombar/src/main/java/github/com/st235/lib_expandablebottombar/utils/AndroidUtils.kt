package github.com.st235.lib_expandablebottombar.utils

import android.os.Build
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup

typealias Scope = () -> Unit

internal inline fun applyForApiLAndHigher(scope: Scope) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        scope()
    }
}
