package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.createChain
import github.com.st235.lib_expandablebottombar.utils.toPx

internal open class ExpandableItemViewController(
    internal val menuItem: ExpandableBottomBarMenuItem,
    private val itemView: View,
    private val textView: TextView,
    private val iconView: ImageView,
    private val backgroundCornerRadius: Float,
    private val backgroundOpacity: Float
) {

    fun deselect() {
        itemView.background = null
        textView.visibility = View.GONE
        textView.isSelected = false
        iconView.isSelected = false
        itemView.isSelected = false
    }

    fun select() {
        itemView.background = createHighlightedMenuShape()
        textView.visibility = View.VISIBLE
        textView.isSelected = true
        iconView.isSelected = true
        itemView.isSelected = true
    }

    @VisibleForTesting
    internal open fun createHighlightedMenuShape(): Drawable {
        return DrawableHelper.createShapeDrawable(
            menuItem.activeColor,
            backgroundCornerRadius,
            backgroundOpacity
        )
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
        private var backgroundCornerRadius: Float = 0.0f
        @FloatRange(from = 0.0, to = 1.0)
        private var backgroundOpacity: Float = 1.0f

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

        fun build(context: Context): ExpandableItemViewController {
            val itemView = LinearLayout(context).apply {
                id = menuItem.itemId
                orientation = LinearLayout.HORIZONTAL
                setPadding(itemHorizontalPadding, itemVerticalPadding, itemHorizontalPadding, itemVerticalPadding)
            }

            val iconView = AppCompatImageView(context).apply {
                setImageDrawable(
                    DrawableHelper.createDrawable(
                        context,
                        menuItem.iconId,
                        backgroundColorSelector
                    )
                )
            }

            val textView = AppCompatTextView(context).apply {
                val rawText = SpannableString(menuItem.text)
                rawText.setSpan(StyleSpan(Typeface.BOLD), 0, rawText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setTextColor(backgroundColorSelector)
                text = rawText
                gravity = Gravity.CENTER
                visibility = View.GONE
                textSize = 15F
            }

            val textLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
                setMargins(8.toPx(), 0, 0, 0)
            }

            with(itemView) {
                addView(iconView)
                addView(textView, textLayoutParams)
                setOnClickListener(onItemClickListener)
            }

            return ExpandableItemViewController(
                menuItem,
                itemView, textView, iconView,
                backgroundCornerRadius, backgroundOpacity
            )
        }
    }
}
