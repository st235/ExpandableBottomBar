package github.com.st235.lib_expandablebottombar

import android.animation.Animator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.os.Parcelable
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar.ItemStyle.Companion.toItemStyle
import github.com.st235.lib_expandablebottombar.behavior.ExpandableBottomBarBehavior
import github.com.st235.lib_expandablebottombar.parsers.ExpandableBottomBarParser
import github.com.st235.lib_expandablebottombar.state.SavedState
import github.com.st235.lib_expandablebottombar.utils.AnimationHelper
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.StyleController
import github.com.st235.lib_expandablebottombar.utils.applyForApiLAndHigher
import github.com.st235.lib_expandablebottombar.utils.clamp
import github.com.st235.lib_expandablebottombar.utils.min
import github.com.st235.lib_expandablebottombar.utils.toPx


internal const val ITEM_NOT_SELECTED = -1

typealias OnItemClickListener = (v: View, menuItem: ExpandableBottomBarMenuItem) -> Unit

/**
 * Widget, which implements bottom bar navigation pattern
 */
class ExpandableBottomBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.exb_expandableButtonBarDefaultStyle
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    internal enum class ItemStyle(private val id: Int) {
        NORMAL(0), OUTLINE(1), STROKE(2);

        companion object {

            fun Int.toItemStyle(): ItemStyle = values().find { it.id == this }
                ?: throw IllegalArgumentException("Cannot find style for id $this")

        }
    }

    private val bounds = Rect()

    @FloatRange(from = 0.0, to = 1.0) private var itemBackgroundOpacity: Float = 0F
    @FloatRange(from = 0.0) private var itemBackgroundCornerRadius: Float = 0F
    @IntRange(from = 0) private var menuItemHorizontalMargin: Int = 0
    @IntRange(from = 0) private var menuItemVerticalMargin: Int = 0
    @IntRange(from = 0) private var menuHorizontalPadding: Int = 0
    @IntRange(from = 0) private var menuVerticalPadding: Int = 0

    @ColorInt private var itemInactiveColor: Int = Color.BLACK
    @FloatRange(from = 0.0) private var backgroundCornerRadius: Float = 0F
    set(value) {
        field = value
        applyForApiLAndHigher {
            invalidateOutline()
        }
    }

    @ColorInt
    private var globalBadgeColor: Int = Color.RED
    @ColorInt
    private var globalBadgeTextColor: Int = Color.WHITE

    private var transitionDuration: Int = 0

    @IdRes private var selectedItemId: Int = ITEM_NOT_SELECTED

    private val menuItems = mutableListOf<ExpandableBottomBarMenuItem>()
    private val viewControllers: MutableMap<Int, ExpandableItemViewController> = mutableMapOf()
    private val stateController = ExpandableBottomBarStateController(this)
    private lateinit var styleController: StyleController

    var onItemSelectedListener: OnItemClickListener? = null
    var onItemReselectedListener: OnItemClickListener? = null

    private var animator: Animator? = null

    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> =
        ExpandableBottomBarBehavior<ExpandableBottomBar>()

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs == null) {
            return
        }

        if (id == View.NO_ID) {
            id = View.generateViewId()
        }

        contentDescription = resources.getString(R.string.accessibility_description)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableBottomBar,
            defStyleAttr, R.style.ExpandableBottomBar)

        itemBackgroundOpacity = typedArray.getFloat(R.styleable.ExpandableBottomBar_exb_itemBackgroundOpacity, 0.2F)
        itemBackgroundCornerRadius = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_itemBackgroundCornerRadius, 30F.toPx())
        transitionDuration = typedArray.getInt(R.styleable.ExpandableBottomBar_exb_transitionDuration, 100)
        itemInactiveColor = typedArray.getColor(R.styleable.ExpandableBottomBar_exb_itemInactiveColor, Color.BLACK)
        menuItemHorizontalMargin = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_horizontal_margin, 5F.toPx()).toInt()
        menuItemVerticalMargin = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_vertical_margin, 5F.toPx()).toInt()
        menuHorizontalPadding = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_horizontal_padding, 15F.toPx()).toInt()
        menuVerticalPadding = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_item_vertical_padding, 10F.toPx()).toInt()

        globalBadgeColor = typedArray.getColor(R.styleable.ExpandableBottomBar_exb_notificationBadgeBackgroundColor, Color.RED)
        globalBadgeTextColor = typedArray.getColor(R.styleable.ExpandableBottomBar_exb_notificationBadgeTextColor, Color.WHITE)

        val rawItemsStyle = typedArray.getInt(R.styleable.ExpandableBottomBar_exb_itemStyle, 0)
        styleController = StyleController.create(style = rawItemsStyle.toItemStyle())

        val backgroundColor = typedArray.getColor(R.styleable.ExpandableBottomBar_exb_backgroundColor, Color.WHITE)
        backgroundCornerRadius = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_backgroundCornerRadius, 0F)

        background =
            DrawableHelper.createShapeDrawable(color = backgroundColor, cornerRadius = backgroundCornerRadius, opacity = 1.0F)

        applyForApiLAndHigher {
            elevation = typedArray.getDimension(R.styleable.ExpandableBottomBar_exb_elevation, 16F.toPx())
            outlineProvider = ExpandableBottomBarOutlineProvider()
            clipToOutline = true
        }

        val menuId = typedArray.getResourceId(R.styleable.ExpandableBottomBar_exb_items, View.NO_ID)
        if (menuId != View.NO_ID) {
            val barParser = ExpandableBottomBarParser(context)
            val items = barParser.inflate(menuId)
            addItems(items)
        }

        typedArray.recycle()
    }

    override fun setBackgroundColor(@ColorInt color: Int) {
        setBackgroundColor(color, backgroundCornerRadius)
    }

    fun setBackgroundColor(@ColorInt color: Int, @FloatRange(from = 0.0) backgroundCornerRadius: Float) {
        this.backgroundCornerRadius = backgroundCornerRadius
        background =
            DrawableHelper.createShapeDrawable(color = color, cornerRadius = backgroundCornerRadius, opacity = 1.0F)
    }

    fun setBackgroundColorRes(@ColorRes colorRes: Int) {
        setBackgroundColor(ContextCompat.getColor(context, colorRes), backgroundCornerRadius)
    }

    fun setBackgroundColorRes(@ColorRes colorRes: Int, @DimenRes backgroundCornerRadiusRes: Int) {
        setBackgroundColor(ContextCompat.getColor(context, colorRes), resources.getDimension(backgroundCornerRadiusRes))
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bounds.set(0, 0, w, h)
    }

    fun getNotificationFor(@IdRes id: Int): ExpandableBottomBarNotification {
        return viewControllers.getValue(id).notification()
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

    /**
     * Shows the bottom bar
     */
    fun show() {
        cancelRunningAnimation()

        animator = AnimationHelper.translateViewTo(this, 0F)
        animator?.start()
    }

    /**
     * Hides the bottom bar
     */
    fun hide() {
        cancelRunningAnimation()

        animator = AnimationHelper.translateViewTo(this, getMaxScrollDistance())
        animator?.start()
    }

    private fun getMaxScrollDistance(): Float {
        val childHeight = if (ViewCompat.isLaidOut(this)) height else measuredHeight
        return childHeight.toFloat() + marginBottom
    }

    private fun cancelRunningAnimation() {
        if (animator?.isRunning == true) {
            animator?.cancel()
            animator = null
        }
    }

    private fun madeMenuItemsAccessible(items: List<ExpandableBottomBarMenuItem>) {
        for ((i, item) in items.withIndex()) {
            val prev = viewControllers[items.getOrNull(i - 1)?.itemId]
            val next = viewControllers[items.getOrNull(i + 1)?.itemId]

            viewControllers[item.itemId]?.setAccessibleWith(prev = prev, next = next)
        }
    }

    private fun createItem(menuItem: ExpandableBottomBarMenuItem): ExpandableItemViewController {
        val viewController =
            ExpandableItemViewController.Builder(menuItem)
                .styleController(styleController)
                .itemMargins(menuHorizontalPadding, menuVerticalPadding)
                .itemBackground(itemBackgroundCornerRadius, itemBackgroundOpacity)
                .itemInactiveColor(itemInactiveColor)
                .notificationBadgeColor(globalBadgeColor)
                .notificationBadgeTextColor(globalBadgeTextColor)
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
        viewControllers.getValue(selectedItemId).unselect()
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class ExpandableBottomBarOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(bounds, clamp(backgroundCornerRadius, 0F, min(height, width) / 2F))
        }
    }
}
