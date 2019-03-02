package github.com.st235.lib_expandablebottombar.utils

import android.os.Build

typealias Scope = () -> Unit

fun applyForApiMAndHigher(scope: Scope) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        scope()
    }
}
