package github.com.st235.lib_expandablebottombar.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

internal class ConstraintLayoutHelper {

    fun layoutParams(
        menuItemHorizontalMargin: Int,
        menuItemVerticalMargin: Int
    ): ConstraintLayout.LayoutParams {
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(
            menuItemHorizontalMargin,
            menuItemVerticalMargin,
            menuItemHorizontalMargin,
            menuItemVerticalMargin
        )

        return lp
    }

    fun applyNewConstraintSetFor(
        self: View,
        parent: ConstraintLayout,
        @IdRes startSiblingId: Int,
        @IdRes endSiblingId: Int
    ) {
        val cl = ConstraintSet()
        cl.clone(parent)

        cl.connect(self.id, ConstraintSet.TOP, parent.id, ConstraintSet.TOP)
        cl.connect(self.id, ConstraintSet.BOTTOM, parent.id, ConstraintSet.BOTTOM)

        if (startSiblingId == self.id) {
            cl.connect(self.id, ConstraintSet.START, parent.id, ConstraintSet.START)
        } else {
            cl.connect(self.id, ConstraintSet.START, startSiblingId, ConstraintSet.END)
            cl.createChain(startSiblingId, self.id, ConstraintSet.CHAIN_PACKED)
        }

        if (endSiblingId == self.id) {
            cl.connect(self.id, ConstraintSet.END, parent.id, ConstraintSet.END)
        } else {
            cl.connect(self.id, ConstraintSet.END, endSiblingId, ConstraintSet.START)
            cl.createChain(self.id, endSiblingId, ConstraintSet.CHAIN_PACKED)
        }

        cl.applyTo(parent)
    }

}