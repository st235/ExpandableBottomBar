package github.com.st235.lib_expandablebottombar

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat.setAccessibilityDelegate
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import github.com.st235.lib_expandablebottombar.utils.createChain
import github.com.st235.lib_expandablebottombar.utils.delayTransition
import github.com.st235.lib_expandablebottombar.utils.show

internal class MenuItemImpl(
        menuItemDescriptor: MenuItemDescriptor,
        private val rootView: ExpandableBottomBar,
        private val itemView: MenuItemView
): MenuItem {

    private val notification = Notification(itemView)

    override val id: Int = menuItemDescriptor.itemId

    override val text: CharSequence = menuItemDescriptor.text

    override val activeColor: Int = menuItemDescriptor.activeColor

    fun setAccessibleWith(prev: MenuItemImpl?,
                          next: MenuItemImpl?) {
        setAccessibilityDelegate(itemView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View?, info: AccessibilityNodeInfoCompat?) {
                info?.setTraversalAfter(prev?.itemView)
                info?.setTraversalBefore(next?.itemView)
                super.onInitializeAccessibilityNodeInfo(host, info)
            }
        })
    }

    override val isShown: Boolean
        get() {
            return itemView.visibility == View.VISIBLE
        }

    override var isAttached: Boolean = false
    private set

    override fun show() {
        rootView.delayTransition()
        itemView.show()
    }

    override fun hide() {
        val menu = rootView.menu

        rootView.delayTransition()
        itemView.show(isShown = false)

        if (menu.selectedItem == this) {
            menu.deselect()
        }
    }

    override fun notification(): Notification {
        return notification
    }

    fun select() {
        itemView.select()
    }

    fun deselect() {
        itemView.deselect()
    }

    fun getView(): View {
        return itemView
    }

    fun attachToSuperView(
        menuItemHorizontalMargin: Int,
        menuItemVerticalMargin: Int
    ) {
        isAttached = true

        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(
            menuItemHorizontalMargin,
            menuItemVerticalMargin,
            menuItemHorizontalMargin,
            menuItemVerticalMargin
        )

        rootView.addView(itemView, lp)
    }

    fun removeFromSuperView() {
        isAttached = false
        rootView.removeView(itemView)
    }

    fun rebuildSiblingsConnection(
        previousIconId: Int,
        nextIconId: Int
    ) {
        val cl = ConstraintSet()
        cl.clone(rootView)

        cl.connect(itemView.id, ConstraintSet.TOP, rootView.id, ConstraintSet.TOP)
        cl.connect(itemView.id, ConstraintSet.BOTTOM, rootView.id, ConstraintSet.BOTTOM)

        if (previousIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.START, rootView.id, ConstraintSet.START)
        } else {
            cl.connect(itemView.id, ConstraintSet.START, previousIconId, ConstraintSet.END)
            cl.createChain(previousIconId, itemView.id, ConstraintSet.CHAIN_PACKED)
        }

        if (nextIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.END, rootView.id, ConstraintSet.END)
        } else {
            cl.connect(itemView.id, ConstraintSet.END, nextIconId, ConstraintSet.START)
            cl.createChain(itemView.id, nextIconId, ConstraintSet.CHAIN_PACKED)
        }

        cl.applyTo(rootView)
    }
}
