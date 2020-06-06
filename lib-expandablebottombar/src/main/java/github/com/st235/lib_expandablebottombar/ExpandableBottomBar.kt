package github.com.st235.lib_expandablebottombar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Parcelable
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import github.com.st235.lib_expandablebottombar.behavior.ExpandableBottomBarBehavior
import github.com.st235.lib_expandablebottombar.parsers.ExpandableBottomBarParser
import github.com.st235.lib_expandablebottombar.state.SavedState
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.applyForApiLAndHigher
import github.com.st235.lib_expandablebottombar.utils.toPx


internal const val ITEM_NOT_SELECTED = -1

typealias OnItemClickListener = (v: View, menuItem: ExpandableBottomBarMenuItem) -> Unit

/**
 * Widget, which implements bottom bar navigation pattern
 */
class ExpandableBottomBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.exb_expandableButtonBarDefaultStyle
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    @FloatRange(from = 0.0, to = 1.0) private var backgroundOpacity: Float = 0F
    @FloatRange(from = 0.0) private var backgroundCornerRadius: Float = 0F
    @IntRange(from = 0) private var menuItemHorizontalMargin: Int = 0
    @IntRange(from = 0) private var menuItemVerticalMargin: Int = 0
    @IntRange(from = 0) private var menuHorizontalPadding: Int = 0
    @IntRange(from = 0) private var menuVerticalPadding: Int = 0

    @ColorInt private var itemInactiveColor: Int = Color.BLACK
    private val backgroundStates
            = arrayOf(
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(-android.R.attr.state_selected)
    )

    private var transitionDuration: Int = 0

    @IdRes private var selectedItemId: Int = ITEM_NOT_SELECTED

    private val menuItems = mutableListOf<ExpandableBottomBarMenuItem>()
    private val viewControllers: MutableMap<Int, ExpandableItemViewController> = mutableMapOf()
    private val stateController = ExpandableBottomBarStateController(this)

    var onItemSelectedListener: OnItemClickListener? = null
    var onItemReselectedListener: OnItemClickListener? = null

    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> =
        ExpandableBottomBarBehavior<ExpandableBottomBar>()

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs == null) {
            return
        }

        contentDescription = resources.getString(R.string.accessibility_description)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableBottomBar,
            defStyleAttr, R.style.ExpandableBottomBar)

        backgroundOpacity = typedArray.getFloat(R.styleable.ExpandableBottomBar_exb_itemBackgroundOpacity, 0.2F)
        backgroundCornerRadius = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_itemBackgroundCornerRadius, 30F.toPx())
        transitionDuration = typedArray.getInt(R.styleable.ExpandableBottomBar_exb_transitionDuration, 100)
        itemInactiveColor = typedArray.getColor(R.styleable.ExpandableBottomBar_exb_itemInactiveColor, Color.BLACK)
        menuItemHorizontalMargin = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_horizontal_margin, 5F.toPx()).toInt()
        menuItemVerticalMargin = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_vertical_margin, 5F.toPx()).toInt()
        menuHorizontalPadding = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_horizontal_padding, 15F.toPx()).toInt()
        menuVerticalPadding = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_vertical_padding, 10F.toPx()).toInt()

        val backgroundColor = typedArray.getColor(R.styleable.ExpandableBottomBar_exb_backgroundColor, Color.WHITE)
        val backgroundCornerRadius = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_backgroundCornerRadius, 0F)

        background =
            DrawableHelper.createShapeDrawable(backgroundColor, backgroundCornerRadius, 1.0F)

        applyForApiLAndHigher {
            elevation = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_elevation, 16F.toPx())
        }

        val menuId = typedArray.getResourceId(R.styleable.ExpandableBottomBar_exb_items, View.NO_ID)
        if (menuId != View.NO_ID) {
            val barParser = ExpandableBottomBarParser(context)
            val items = barParser.inflate(menuId)
            addItems(items)
        }

        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val lp = layoutParams
        if (lp is CoordinatorLayout.LayoutParams) {
            lp.insetEdge = Gravity.BOTTOM
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return stateController.store(superState)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        stateController.restore(state)
    }

    /**
     * Adds passed items to widget
     *
     * @param items - bottom bar menu items
     */
    fun addItems(items: List<ExpandableBottomBarMenuItem>) {
        menuItems.clear()

        val firstItemId = items.first().itemId
        val lastItemId = items.last().itemId
        selectedItemId = firstItemId

        for ((i, item) in items.withIndex()) {
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

        menuItems.addAll(items)
        madeMenuItemsAccessible(items)
    }

    internal fun getMenuItems(): List<ExpandableBottomBarMenuItem> = menuItems

    /**
     * Programmatically select item
     *
     * @param id - identifier of menu item, which should be selected
     */
    fun select(@IdRes id: Int) {
        val itemToSelect = viewControllers.getValue(id)
        onItemSelected(itemToSelect.menuItem)
    }

    /**
     * Returns currently selected item
     */
    fun getSelected(): ExpandableBottomBarMenuItem = viewControllers.getValue(selectedItemId).menuItem

    private fun madeMenuItemsAccessible(items: List<ExpandableBottomBarMenuItem>) {
        for ((i, item) in items.withIndex()) {
            val prev = viewControllers[items.getOrNull(i - 1)?.itemId]
            val next = viewControllers[items.getOrNull(i + 1)?.itemId]

            viewControllers[item.itemId]?.setAccessibleWith(prev = prev, next = next)
        }
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
                    if (!v.isSelected) {
                        onItemSelected(menuItem)
                        onItemSelectedListener?.invoke(v, menuItem)
                    } else {
                        onItemReselectedListener?.invoke(v, menuItem)
                    }
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

    internal class ExpandableBottomBarStateController(
        private val expandableBottomBar: ExpandableBottomBar
    ) {

        fun store(superState: Parcelable?) = SavedState(expandableBottomBar.selectedItemId, superState)

        fun restore(state: SavedState) {
            val selectedItemId = state.selectedItem
            val viewController = expandableBottomBar.viewControllers.getValue(selectedItemId)
            expandableBottomBar.onItemSelected(viewController.menuItem)
        }
    }
}