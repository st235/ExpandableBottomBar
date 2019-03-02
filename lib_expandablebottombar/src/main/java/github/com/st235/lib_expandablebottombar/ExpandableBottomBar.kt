package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.applyForApiMAndHigher
import github.com.st235.lib_expandablebottombar.utils.createChain
import github.com.st235.lib_expandablebottombar.utils.toPx

internal const val ITEM_NOT_SELECTED = -1

internal const val TEXT_TAG = "tag.text"
internal const val IMAGE_TAG = "tag.image"

typealias OnItemClickListener = (v: View, menuItem: ExpandableBottomBarMenuItem) -> Unit

class ExpandableBottomBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.expandableButtonBarDefaultStyle
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var backgroundOpacity: Float = 0F
    private var backgroundCornerRadius: Float = 0F
    private var menuItemHorizontalMargin: Int = 0
    private var menuItemVerticalMargin: Int = 0
    private var menuHorizontalPadding: Int = 0
    private var menuVerticalPadding: Int = 0
    @ColorInt private var inactiveBackgroundColor: Int = Color.BLACK
    private val backgroundStates
            = arrayOf(intArrayOf(android.R.attr.state_selected),
            intArrayOf(-android.R.attr.state_selected))

    private var transitionDuration: Int = 0

    @IdRes private var selectedItemId: Int = ITEM_NOT_SELECTED

    var onItemClickListener: OnItemClickListener? = null

    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableBottomBar,
            defStyleAttr, R.style.ExpandableBottomBar)

        backgroundOpacity = typedArray.getFloat(R.styleable.ExpandableBottomBar_itemBackgroundOpacity, 0.2F)
        backgroundCornerRadius = typedArray.getDimension(R.styleable.ExpandableBottomBar_itemBackgroundCornerRadius, 30F.toPx())
        inactiveBackgroundColor = typedArray.getColor(R.styleable.ExpandableBottomBar_itemInactiveBackgroundColor, Color.BLACK)
        transitionDuration = typedArray.getInt(R.styleable.ExpandableBottomBar_transitionDuration, 100)
        menuItemHorizontalMargin = typedArray.getDimension(R.styleable.ExpandableBottomBar_item_horizontal_margin, 5F.toPx()).toInt()
        menuItemVerticalMargin = typedArray.getDimension(R.styleable.ExpandableBottomBar_item_vertical_margin, 5F.toPx()).toInt()
        menuHorizontalPadding = typedArray.getDimension(R.styleable.ExpandableBottomBar_item_horizontal_padding, 15F.toPx()).toInt()
        menuVerticalPadding = typedArray.getDimension(R.styleable.ExpandableBottomBar_item_vertical_padding, 10F.toPx()).toInt()

        val backgroundColor = typedArray.getColor(R.styleable.ExpandableBottomBar_backgroundColor, Color.WHITE)
        val backgroundCornerRadius = typedArray.getDimension(R.styleable.ExpandableBottomBar_backgroundCornerRadius, 0F)

        background =
            DrawableHelper.createShapeDrawable(backgroundColor, backgroundCornerRadius, 1.0F)

        applyForApiMAndHigher {
            elevation = typedArray.getDimension(R.styleable.ExpandableBottomBar_elevation, 16F.toPx())
        }

        typedArray.recycle()
    }

    fun addItems(items: List<ExpandableBottomBarMenuItem>) {
        val firstItemId = items.first().itemId
        val lastItemId = items.last().itemId
        selectedItemId = firstItemId

        for (i in 0 until items.size) {
            val item = items[i]
            val itemView = createItem(item)

            val prevIconId = if (i - 1 < 0) firstItemId else items[i - 1].itemId
            val nextIconId = if (i + 1 >= items.size) lastItemId else items[i + 1].itemId

            addItemInternal(itemView, prevIconId, nextIconId)
        }
    }

    private fun onItemClickInternal(
        activeMenuItem: ExpandableBottomBarMenuItem,
        activeItemView: View,
        activeIconView: AppCompatImageView,
        activeTextView: TextView
    ) {
        if (selectedItemId == activeMenuItem.itemId) {
            return
        }

        applyTransition()

        val set = ConstraintSet()
        set.clone(this)

        selectItem(activeMenuItem, activeItemView, activeIconView, activeTextView)

        val previouslyItemView = findViewById<LinearLayout>(selectedItemId)
        val previouslyTextView = previouslyItemView.findViewWithTag<View>(TEXT_TAG) as TextView
        val previouslyActiveIconView = previouslyItemView.findViewWithTag<View>(IMAGE_TAG) as AppCompatImageView

        deselectItem(previouslyItemView, previouslyActiveIconView, previouslyTextView)

        selectedItemId = activeMenuItem.itemId
        set.applyTo(this)
    }

    private fun createItem(menuItem: ExpandableBottomBarMenuItem): View {
        val itemView = LinearLayout(context).apply {
            id = menuItem.itemId
            orientation = LinearLayout.HORIZONTAL
            setPadding(menuHorizontalPadding, menuVerticalPadding,
                    menuHorizontalPadding, menuVerticalPadding)
        }

        val colors = intArrayOf(menuItem.activeColor, inactiveBackgroundColor)
        val selectedStateColorList = ColorStateList(backgroundStates, colors)

        val iconView = AppCompatImageView(context).apply {
            tag = IMAGE_TAG
            setImageDrawable(DrawableHelper.createDrawable(context, menuItem.iconId, selectedStateColorList))
        }

        val textView = AppCompatTextView(context).apply {
            val rawText = SpannableString(resources.getString(menuItem.textId))
            rawText.setSpan(StyleSpan(BOLD), 0, rawText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setTextColor(selectedStateColorList)
            text = rawText
            tag = TEXT_TAG
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

        if (selectedItemId == menuItem.itemId) {
            selectItem(menuItem, itemView, iconView, textView)
        }

        with(itemView) {
            addView(iconView)
            addView(textView, textLayoutParams)
            setOnClickListener {
                onItemClickInternal(menuItem, itemView, iconView, textView)
                onItemClickListener?.invoke(itemView, menuItem)
            }
        }

        return itemView
    }

    private fun addItemInternal(
        itemView: View,
        previousIconId: Int,
        nextIconId: Int
    ) {
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(menuItemHorizontalMargin, menuItemVerticalMargin,
                menuItemHorizontalMargin, menuItemVerticalMargin)

        addView(itemView, lp)

        val cl = ConstraintSet()
        cl.clone(this)

        cl.connect(itemView.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
        cl.connect(itemView.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)

        if (previousIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.START, id, ConstraintSet.START)
        } else {
            cl.connect(itemView.id, ConstraintSet.START, previousIconId, ConstraintSet.END)
            cl.createChain(previousIconId, itemView.id, ConstraintSet.CHAIN_PACKED)
        }

        if (nextIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.END, id, ConstraintSet.END)
        } else {
            cl.connect(itemView.id, ConstraintSet.END, nextIconId, ConstraintSet.START)
            cl.createChain(itemView.id, nextIconId, ConstraintSet.CHAIN_PACKED)
        }

        cl.applyTo(this)
    }

    private fun deselectItem(
        itemView: View,
        iconView: AppCompatImageView,
        textView: TextView
    ) {
        itemView.background = null
        textView.visibility = View.GONE
        textView.isSelected = false
        iconView.isSelected = false
    }

    private fun selectItem(
        menuItem: ExpandableBottomBarMenuItem,
        itemView: View,
        iconView: AppCompatImageView,
        textView: TextView
    ) {
        itemView.background =
            DrawableHelper.createShapeDrawable(menuItem.activeColor, backgroundCornerRadius, backgroundOpacity)
        textView.visibility = View.VISIBLE
        textView.isSelected = true
        iconView.isSelected = true
    }

    private fun applyTransition() {
        val autoTransition = AutoTransition()
        autoTransition.duration = transitionDuration.toLong()
        TransitionManager.beginDelayedTransition(this, autoTransition)
    }
}