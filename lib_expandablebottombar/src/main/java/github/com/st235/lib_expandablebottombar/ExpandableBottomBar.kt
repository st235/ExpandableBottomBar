package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import github.com.st235.lib_expandablebottombar.utils.*

internal const val ITEM_NOT_SELECTED = -1

typealias OnItemClickListener = (v: View, menuItem: ExpandableBottomBarMenuItem) -> Unit

/**
 * Widget, which implements bottom bar navigation pattern
 */
class ExpandableBottomBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.expandableButtonBarDefaultStyle
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var backgroundOpacity: Float = 0F
    private var backgroundCornerRadius: Float = 0F
    private var menuItemHorizontalMargin: Int = 0
    private var menuItemVerticalMargin: Int = 0
    private var menuHorizontalPadding: Int = 0
    private var menuVerticalPadding: Int = 0
    @ColorInt private var itemInactiveColor: Int = Color.BLACK
    private val backgroundStates
            = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf(-android.R.attr.state_selected))

    private var transitionDuration: Int = 0

    @IdRes private var selectedItemId: Int = ITEM_NOT_SELECTED
    private val viewControllers: MutableMap<Int, ExpandableItemViewController> = mutableMapOf()

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
        transitionDuration = typedArray.getInt(R.styleable.ExpandableBottomBar_transitionDuration, 100)
        itemInactiveColor = typedArray.getColor(R.styleable.ExpandableBottomBar_itemInactiveColor, Color.BLACK)
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

    /**
     * Adds passed items to widget
     *
     * @param items - bottom bar menu items
     */
    fun addItems(items: List<ExpandableBottomBarMenuItem>) {
        val firstItemId = items.first().itemId
        val lastItemId = items.last().itemId
        selectedItemId = firstItemId

        for (i in 0 until items.size) {
            val item = items[i]
            val viewController = createItem(item)
            viewControllers[item.itemId] = viewController

            val prevIconId = if (i - 1 < 0) firstItemId else items[i - 1].itemId
            val nextIconId = if (i + 1 >= items.size) lastItemId else items[i + 1].itemId

            viewController.attachTo(
                this,
                prevIconId, nextIconId,
                menuItemHorizontalMargin, menuItemVerticalMargin
            )
        }
    }

    /**
     * Programmatically select item
     *
     * @param id - identifier of menu item, which should be selected
     */
    fun select(@IdRes id: Int) {
        val itemToSelect = viewControllers.getValue(id)
        onItemSelected(itemToSelect.menuItem)
    }

    private fun createItem(menuItem: ExpandableBottomBarMenuItem): ExpandableItemViewController {
        val colors = intArrayOf(menuItem.activeColor, itemInactiveColor)
        val selectedStateColorList = ColorStateList(backgroundStates, colors)

        val viewController =
            ExpandableItemViewController.Builder(menuItem)
                .itemMargins(menuHorizontalPadding, menuVerticalPadding)
                .itemBackground(backgroundCornerRadius, backgroundOpacity)
                .itemsColors(selectedStateColorList)
                .onItemClickListener { v: View ->
                    onItemSelected(menuItem)
                    onItemClickListener?.invoke(v, menuItem)
                }
                .build(context)

        if (selectedItemId == menuItem.itemId) {
            viewController.select()
        }

        return viewController
    }

    private fun onItemSelected(activeMenuItem: ExpandableBottomBarMenuItem) {
        if (selectedItemId == activeMenuItem.itemId) {
            return
        }

        applyTransition()

        val set = ConstraintSet()
        set.clone(this)

        viewControllers.getValue(activeMenuItem.itemId).select()
        viewControllers.getValue(selectedItemId).deselect()
        selectedItemId = activeMenuItem.itemId

        set.applyTo(this)
    }

    private fun applyTransition() {
        val autoTransition = AutoTransition()
        autoTransition.duration = transitionDuration.toLong()
        TransitionManager.beginDelayedTransition(this, autoTransition)
    }
}