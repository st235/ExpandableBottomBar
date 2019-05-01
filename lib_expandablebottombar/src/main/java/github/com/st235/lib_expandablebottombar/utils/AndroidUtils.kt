package github.com.st235.lib_expandablebottombar.utils

import android.os.Build

typealias Scope = () -> Unit

internal inline fun applyForApiLAndHigher(scope: Scope) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        scope()
    }
}
