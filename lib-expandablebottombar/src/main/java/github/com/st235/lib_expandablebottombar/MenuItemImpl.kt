package github.com.st235.lib_expandablebottombar

import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat.setAccessibilityDelegate
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import github.com.st235.lib_expandablebottombar.utils.ConstraintLayoutHelper
import github.com.st235.lib_expandablebottombar.utils.TransitionHelper

internal class MenuItemImpl(
        menuItemDescriptor: MenuItemDescriptor,
        private val rootView: ExpandableBottomBar,
        private val itemView: MenuItemView,
        private val transitionHelper: TransitionHelper = TransitionHelper(),
        private val constraintLayoutHelper: ConstraintLayoutHelper = ConstraintLayoutHelper()
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
        transitionHelper.apply(rootView)
        itemView.visibility = View.VISIBLE
    }

    override fun hide() {
        val menu = rootView.menu

        transitionHelper.apply(rootView)
        itemView.visibility = View.GONE

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

        val lp = constraintLayoutHelper.layoutParams(
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
        @IdRes previousIconId: Int,
        @IdRes nextIconId: Int
    ) {
        constraintLayoutHelper.applyNewConstraintSetFor(
            itemView,
            rootView,
            previousIconId,
            nextIconId
        )
    }

}
