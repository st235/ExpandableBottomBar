package github.com.st235.lib_expandablebottombar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat.setAccessibilityDelegate
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import github.com.st235.lib_expandablebottombar.components.ExpandableBottomBarItemView
import github.com.st235.lib_expandablebottombar.utils.StyleController
import github.com.st235.lib_expandablebottombar.utils.createChain

internal open class ExpandableItemViewController(
    internal val menuItem: ExpandableBottomBarMenuItem,
    private val itemView: ExpandableBottomBarItemView
) {

    fun setAccessibleWith(prev: ExpandableItemViewController?,
                          next: ExpandableItemViewController?) {
        setAccessibilityDelegate(itemView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View?, info: AccessibilityNodeInfoCompat?) {
                info?.setTraversalAfter(prev?.itemView)
                info?.setTraversalBefore(next?.itemView)
                super.onInitializeAccessibilityNodeInfo(host, info)
            }
        })
    }

    fun unselect() {
        itemView.deselect()
    }

    fun select() {
        itemView.select()
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

    class Builder(private val menuItem: ExpandableBottomBarMenuItem) {

        @Px
        private var itemVerticalPadding: Int = 0
        @Px
        private var itemHorizontalPadding: Int = 0
        @Px
        @SuppressLint("SupportAnnotationUsage")
        private var backgroundCornerRadius: Float = 0.0f
        @FloatRange(from = 0.0, to = 1.0)
        private var backgroundOpacity: Float = 1.0f

        private lateinit var styleController: StyleController
        private lateinit var backgroundColorSelector: ColorStateList
        private lateinit var onItemClickListener: (View) -> Unit

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

        fun itemsColors(backgroundColorSelector: ColorStateList): Builder {
            this.backgroundColorSelector = backgroundColorSelector
            return this
        }

        fun onItemClickListener(onItemClickListener: (View) -> Unit): Builder {
            this.onItemClickListener = onItemClickListener
            return this
        }

        fun styleController(styleController: StyleController): Builder {
            this.styleController = styleController
            return this
        }

        @VisibleForTesting
        internal open fun createHighlightedMenuShape(): Drawable {
            return styleController.createStateBackground(
                menuItem.activeColor,
                backgroundCornerRadius,
                backgroundOpacity
            )
        }

        fun build(context: Context): ExpandableItemViewController {
            val itemView = ExpandableBottomBarItemView(context)

            with(itemView) {
                id = menuItem.itemId
                contentDescription = context.resources.getString(R.string.accessibility_item_description, menuItem.text)
                setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)

                setIcon(menuItem.iconId, backgroundColorSelector)
                setText(menuItem.text, backgroundColorSelector)
                background = createHighlightedMenuShape()
                setOnClickListener(onItemClickListener)
            }

            return ExpandableItemViewController(
                menuItem,
                itemView
            )
        }
    }
}
