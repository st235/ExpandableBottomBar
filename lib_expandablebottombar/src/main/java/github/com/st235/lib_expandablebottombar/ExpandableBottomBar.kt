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
import android.support.v4.graphics.drawable.DrawableCompat
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.createChain
import github.com.st235.lib_expandablebottombar.utils.toPx

internal const val ITEM_NOT_SELECTED = -1

internal const val TEXT_TAG = "tag.text"
internal const val IMAGE_TAG = "tag.image"

typealias OnItemClickListener = (v: View, menuItem: ExpandableBottomBarMenuItem) -> Unit

class ExpandableBottomBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var backgroundOpacity: Float = 0F
    private var backgroundCornerRadius: Float = 0F
    @ColorInt private var inactiveBackgroundColor: Int = Color.BLACK
    private val backgroundStates
            = arrayOf(intArrayOf(android.R.attr.state_selected),
            intArrayOf(-android.R.attr.state_selected))

    private var transitionDuration: Int = 0

    @IdRes private var selectedItemId: Int = ITEM_NOT_SELECTED

    var onItemClickListener: OnItemClickListener? = null

    init {
        initAttrs(context, attrs)
    }


    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableBottomBar)

        backgroundOpacity = typedArray.getFloat(R.styleable.ExpandableBottomBar_backgroundOpacity, 0.2F)
        backgroundCornerRadius = typedArray.getDimension(R.styleable.ExpandableBottomBar_backgroundCornerRadius, 30F.toPx())
        inactiveBackgroundColor = typedArray.getColor(R.styleable.ExpandableBottomBar_inactiveBackgroundColor, Color.BLACK)
        transitionDuration = typedArray.getInt(R.styleable.ExpandableBottomBar_transitionDuration, 100)

        typedArray.recycle()
    }

    fun addItems(items: List<ExpandableBottomBarMenuItem>) {
        val firstItemId = items.first().itemId
        val lastItemId = items.last().itemId
        selectedItemId = firstItemId

        for (i in 0 until items.size) {
            val item = items[i]
            val itemView = createItem(item)

            val prevId = if (i - 1 < 0) firstItemId else items[i - 1].itemId
            val nextId = if (i + 1 >= items.size) lastItemId else items[i + 1].itemId

            addViewInternal(itemView, prevId, nextId)
        }
    }

    private fun onItemClick(
            activeItemId: Int,
            activeItemView: View,
            activeIconView: AppCompatImageView,
            activeTextView: TextView,
            activeColor: Int
    ) {
        if (selectedItemId == activeItemId) {
            return
        }

        applyTransition()

        val set = ConstraintSet()
        set.clone(this)

        val previouslyItemView = findViewById<LinearLayout>(selectedItemId)
        val previouslyTextView = previouslyItemView.findViewWithTag<View>(TEXT_TAG) as TextView
        val previouslyActiveIconView = previouslyItemView.findViewWithTag<View>(IMAGE_TAG) as ImageView

        activeItemView.background = DrawableHelper.createShapeDrawable(activeColor, backgroundCornerRadius, backgroundOpacity)
        activeIconView.isSelected = true
        activeTextView.isSelected = true
        activeTextView.visibility = View.VISIBLE

        previouslyItemView.background = null
        previouslyTextView.visibility = View.GONE
        previouslyTextView.isSelected = false
        previouslyActiveIconView.isSelected = false

        selectedItemId = activeItemId
        set.applyTo(this)
    }

    private fun createItem(menuItem: ExpandableBottomBarMenuItem): View {
        val item = LinearLayout(context).apply {
            id = menuItem.itemId
            orientation = LinearLayout.HORIZONTAL
        }

        val colors = intArrayOf(menuItem.activeColor, inactiveBackgroundColor)
        val selectedStateColorList = ColorStateList(backgroundStates, colors)

        val iconView = AppCompatImageView(context).apply {
            tag = IMAGE_TAG
            setImageDrawable(DrawableHelper.createDrawable(context, menuItem.iconId,
                    selectedStateColorList))
        }

        val text = AppCompatTextView(context).apply {
            val rawText = SpannableString(resources.getString(menuItem.textId))
            rawText.setSpan(StyleSpan(BOLD), 0, rawText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setTextColor(selectedStateColorList)
            text = rawText
            tag = TEXT_TAG
            gravity = Gravity.CENTER
            visibility = View.GONE
        }

        if (selectedItemId == menuItem.itemId) {
            item.background = DrawableHelper.createShapeDrawable(menuItem.activeColor, backgroundCornerRadius, backgroundOpacity)
            text.visibility = View.VISIBLE
            text.isSelected = true
            val drawable = ContextCompat.getDrawable(context, menuItem.iconId)
            iconView.isSelected = true
            iconView.setImageDrawable(drawable)
        }

        val textLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            gravity = Gravity.CENTER
            setMargins(6.toPx(), 0, 0, 0)
        }

        with(item) {
            addView(iconView)
            addView(text, textLp)
            setOnClickListener {
                onItemClick(menuItem.itemId, item, iconView, text, menuItem.activeColor)
                onItemClickListener?.invoke(item, menuItem)
            }
        }

        return item
    }

    private fun addViewInternal(
        icon: View,
        previousId: Int,
        nextId: Int
    ) {
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        icon.setPadding(15.toPx(), 10.toPx(), 15.toPx(), 10.toPx())
        lp.setMargins(5.toPx(), 5.toPx(), 5.toPx(), 5.toPx())

        addView(icon, lp)

        val cl = ConstraintSet()
        cl.clone(this)

        cl.connect(icon.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
        cl.connect(icon.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)

        if (previousId == icon.id) {
            cl.connect(icon.id, ConstraintSet.START, id, ConstraintSet.START)
        } else {
            cl.connect(icon.id, ConstraintSet.START, previousId, ConstraintSet.END)
            cl.createChain(previousId, icon.id, ConstraintSet.CHAIN_PACKED)
        }

        if (nextId == icon.id) {
            cl.connect(icon.id, ConstraintSet.END, id, ConstraintSet.END)
        } else {
            cl.connect(icon.id, ConstraintSet.END, nextId, ConstraintSet.START)
            cl.createChain(icon.id, nextId, ConstraintSet.CHAIN_PACKED)
        }

        cl.applyTo(this)
    }

    private fun applyTransition() {
        val autoTransition = AutoTransition()
        autoTransition.duration = transitionDuration.toLong()
        TransitionManager.beginDelayedTransition(this, autoTransition)
    }
}