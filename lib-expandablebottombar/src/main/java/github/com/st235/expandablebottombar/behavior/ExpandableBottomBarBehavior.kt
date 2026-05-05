package github.com.st235.lib_expandablebottombar.behavior

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

open class ExpandableBottomBarBehavior<V: View>: CoordinatorLayout.Behavior<V> {

    constructor(): super()

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (dependency is Snackbar.SnackbarLayout) {
            updateSnackbar(child, dependency)
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    /**
     * Lint is suppressed to modify
     * snackbar default behaviour. Snackbar is
     * overlaying [ExpandableBottomBar] without
     * this changes.
     */
    @SuppressLint("RestrictedApi")
    private fun updateSnackbar(child: View, snackbarLayout: Snackbar.SnackbarLayout) {
        val params = snackbarLayout.layoutParams

        if (params is CoordinatorLayout.LayoutParams) {

            params.anchorId = child.id
            params.anchorGravity = Gravity.TOP
            params.gravity = Gravity.TOP
            snackbarLayout.layoutParams = params
        }
    }
}
