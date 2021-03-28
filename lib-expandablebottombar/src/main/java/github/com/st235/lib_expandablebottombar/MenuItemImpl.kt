package github.com.st235.lib_expandablebottombar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat.setAccessibilityDelegate
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import github.com.st235.lib_expandablebottombar.components.MenuItemView
import github.com.st235.lib_expandablebottombar.utils.*
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.StyleController
import github.com.st235.lib_expandablebottombar.utils.createChain
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

    override fun show() {
        rootView.delayTransition()
        itemView.show()
    }

    override fun hide() {
        if (rootView.getSelected() == this) {
            throw IllegalStateException("Cannot hide active tab")
        }

        rootView.delayTransition()
        itemView.show(isShown = false)
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

    fun attachTo(parent: ConstraintLayout,
                 previousIconId: Int,
                 nextIconId: Int,
                 menuItemHorizontalMargin: Int,
                 menuItemVerticalMargin: Int) {
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(menuItemHorizontalMargin, menuItemVerticalMargin,
            menuItemHorizontalMargin, menuItemVerticalMargin)

        parent.addView(itemView, lp)

        val cl = ConstraintSet()
        cl.clone(parent)

        cl.connect(itemView.id, ConstraintSet.TOP, parent.id, ConstraintSet.TOP)
        cl.connect(itemView.id, ConstraintSet.BOTTOM, parent.id, ConstraintSet.BOTTOM)

        if (previousIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.START, parent.id, ConstraintSet.START)
        } else {
            cl.connect(itemView.id, ConstraintSet.START, previousIconId, ConstraintSet.END)
            cl.createChain(previousIconId, itemView.id, ConstraintSet.CHAIN_PACKED)
        }

        if (nextIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.END, parent.id, ConstraintSet.END)
        } else {
            cl.connect(itemView.id, ConstraintSet.END, nextIconId, ConstraintSet.START)
            cl.createChain(itemView.id, nextIconId, ConstraintSet.CHAIN_PACKED)
        }

        cl.applyTo(parent)
    }

    //TODO(st235): separate this builder to view factory
    class Builder(private val menuItemDescriptor: MenuItemDescriptor) {

        @Px
        private var itemVerticalPadding: Int = 0
        @Px
        private var itemHorizontalPadding: Int = 0
        @Px
        @SuppressLint("SupportAnnotationUsage")
        private var backgroundCornerRadius: Float = 0.0f
        @FloatRange(from = 0.0, to = 1.0)
        private var backgroundOpacity: Float = 1.0f
        @ColorInt
        private var itemInactiveColor: Int = Color.BLACK
        @ColorInt
        private var notificationBadgeColor: Int = Color.RED
        @ColorInt
        private var notificationBadgeTextColor: Int = Color.WHITE

        private lateinit var styleController: StyleController
        private lateinit var onItemClickListener: (MenuItem, View) -> Unit

        fun itemMargins(
            @Px itemHorizontalPadding: Int,
            @Px itemVerticalPadding: Int
        ): Builder {
            this.itemVerticalPadding = itemVerticalPadding
            this.itemHorizontalPadding = itemHorizontalPadding
            return this
        }

        fun itemBackground(backgroundCornerRadius: Float,
                           @FloatRange(from = 0.0, to = 1.0) backgroundOpacity: Float): Builder {
            this.backgroundCornerRadius = backgroundCornerRadius
            this.backgroundOpacity = backgroundOpacity
            return this
        }

        fun itemInactiveColor(@ColorInt itemInactiveColor: Int): Builder {
            this.itemInactiveColor = itemInactiveColor
            return this
        }

        fun onItemClickListener(onItemClickListener: (MenuItem, View) -> Unit): Builder {
            this.onItemClickListener = onItemClickListener
            return this
        }

        fun styleController(styleController: StyleController): Builder {
            this.styleController = styleController
            return this
        }

        fun notificationBadgeColor(@ColorInt notificationBadgeColor: Int): Builder {
            this.notificationBadgeColor = notificationBadgeColor
            return this
        }

        fun notificationBadgeTextColor(@ColorInt notificationBadgeTextColor: Int): Builder {
            this.notificationBadgeTextColor = notificationBadgeTextColor
            return this
        }

        private fun createHighlightedMenuShape(): Drawable {
            return styleController.createStateBackground(
                menuItemDescriptor.activeColor,
                backgroundCornerRadius,
                backgroundOpacity
            )
        }

        private fun createMenuItemView(context: Context): MenuItemView {
            return MenuItemView(context = context)
        }

        fun build(rootView: ExpandableBottomBar): MenuItemImpl {
            val context: Context = rootView.context

            val itemView = createMenuItemView(context)
            val menuItem = MenuItemImpl(
                    menuItemDescriptor,
                    rootView,
                    itemView
            )

            val backgroundColorStateList = DrawableHelper.createSelectedUnselectedStateList(
                menuItemDescriptor.activeColor,
                itemInactiveColor
            )

            with(itemView) {
                id = menuItemDescriptor.itemId
                contentDescription = context.resources.getString(R.string.accessibility_item_description, menuItemDescriptor.text)
                setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)

                setIcon(menuItemDescriptor.iconId, backgroundColorStateList)
                setText(menuItemDescriptor.text, backgroundColorStateList)
                notificationBadgeBackgroundColor = menuItemDescriptor.badgeBackgroundColor ?: notificationBadgeColor
                notificationBadgeTextColor = menuItemDescriptor.badgeTextColor ?: notificationBadgeTextColor

                background = createHighlightedMenuShape()
                setOnClickListener {
                    onItemClickListener(menuItem, it)
                }
            }

            return menuItem
        }
    }
}
