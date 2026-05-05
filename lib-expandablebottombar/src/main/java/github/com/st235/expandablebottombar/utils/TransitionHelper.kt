package github.com.st235.lib_expandablebottombar.utils

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.ViewGroup

internal class TransitionHelper {

    fun apply(view: ViewGroup, duration: Long = -1) {
        val autoTransition = AutoTransition()
        autoTransition.duration = duration
        TransitionManager.beginDelayedTransition(view, autoTransition)
    }
}