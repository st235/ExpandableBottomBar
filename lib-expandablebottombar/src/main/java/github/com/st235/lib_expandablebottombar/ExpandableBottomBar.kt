package github.com.st235.lib_expandablebottombar

import android.animation.Animator
import android.annotation.TargetApi
import android.content.Context
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
import github.com.st235.lib_expandablebottombar.state.BottomBarSavedState
import github.com.st235.lib_expandablebottombar.utils.*
import github.com.st235.lib_expandablebottombar.utils.DrawableHelper
import github.com.st235.lib_expandablebottombar.utils.StyleController
import github.com.st235.lib_expandablebottombar.utils.applyForApiLAndHigher
import github.com.st235.lib_expandablebottombar.utils.clamp
import github.com.st235.lib_expandablebottombar.utils.min
import github.com.st235.lib_expandablebottombar.utils.toPx


internal const val ITEM_NOT_SELECTED = -1

typealias OnItemClickListener = (v: View, menuItem: MenuItem) -> Unit

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

    private val menuItems: MutableMap<Int, MenuItemImpl> = mutableMapOf()
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

    fun setNotificationBadgeBackgroundColor(@ColorInt color: Int) {
        globalBadgeColor = color
        invalidate()
    }

    fun setNotificationBadgeBackgroundColorRes(@ColorRes colorRes: Int) {
        setNotificationBadgeBackgroundColor(ContextCompat.getColor(context, colorRes))
    }

    fun setNotificationBadgeTextColor(@ColorInt color: Int) {
        globalBadgeTextColor = color
        invalidate()
    }

    fun setNotificationBadgeTextColorRes(@ColorRes colorRes: Int) {
        setNotificationBadgeTextColor(ContextCompat.getColor(context, colorRes))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val lp = layoutParams
        if (lp is CoordinatorLayout.LayoutParams) {
            lp.insetEdge = Gravity.BOTTOM
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return stateController.store(superState)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is BottomBarSavedState) {
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

    /**
     * Returns menu item for the given id value
     *
     * @throws NullPointerException when id doesn't exists
     */
    fun getMenuItemFor(@IdRes id: Int): MenuItem {
        return menuItems.getValue(id)
    }

    /**
     * Returns notification for passed menu item
     *
     * @throws NullPointerException when id doesn't exists
     */
    @Deprecated(
            message = "This method was replaced with MenuItem#notification",
            replaceWith = ReplaceWith(expression = "this.getMenuItemFor(id = id).notification()"),
            level = DeprecationLevel.ERROR
    )
    fun getNotificationFor(@IdRes id: Int): Notification {
        return menuItems.getValue(id).notification()
    }

    /**
     * Adds passed items to widget
     *
     * @param itemDescriptors - bottom bar menu items
     */
    fun addItems(itemDescriptors: List<MenuItemDescriptor>) {
        menuItems.clear()

        val firstItemId = itemDescriptors.first().itemId
        val lastItemId = itemDescriptors.last().itemId
        selectedItemId = firstItemId

        for ((i, item) in itemDescriptors.withIndex()) {
            val viewController = createItem(item)
            menuItems[item.itemId] = viewController

            val prevIconId = if (i - 1 < 0) firstItemId else itemDescriptors[i - 1].itemId
            val nextIconId = if (i + 1 >= itemDescriptors.size) lastItemId else itemDescriptors[i + 1].itemId

            viewController.attachTo(
                this,
                prevIconId, nextIconId,
                menuItemHorizontalMargin, menuItemVerticalMargin
            )
        }

        madeMenuItemsAccessible(itemDescriptors)
    }

    /**
     * Returns all menu items
     */
    fun getMenuItems(): List<MenuItem> = menuItems.values.toList()

    /**
     * Programmatically select item
     *
     * @param id - identifier of menu item, which should be selected
     */
    fun select(@IdRes id: Int) {
        val itemToSelect = menuItems.getValue(id)
        onItemSelected(itemToSelect)
    }

    /**
     * Returns currently selected item
     */
    fun getSelected(): MenuItem = menuItems.getValue(selectedItemId)

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

    private fun madeMenuItemsAccessible(itemDescriptors: List<MenuItemDescriptor>) {
        for ((i, item) in itemDescriptors.withIndex()) {
            val prev = menuItems[itemDescriptors.getOrNull(i - 1)?.itemId]
            val next = menuItems[itemDescriptors.getOrNull(i + 1)?.itemId]

            menuItems[item.itemId]?.setAccessibleWith(prev = prev, next = next)
        }
    }

    private fun createItem(menuItemDescriptor: MenuItemDescriptor): MenuItemImpl {
        val menuItem =
            MenuItemImpl.Builder(menuItemDescriptor)
                .styleController(styleController)
                .itemMargins(menuHorizontalPadding, menuVerticalPadding)
                .itemBackground(itemBackgroundCornerRadius, itemBackgroundOpacity)
                .itemInactiveColor(itemInactiveColor)
                .notificationBadgeColor(globalBadgeColor)
                .notificationBadgeTextColor(globalBadgeTextColor)
                .onItemClickListener { menuItem: MenuItem, v: View ->
                    if (!v.isSelected) {
                        onItemSelected(menuItem)
                        onItemSelectedListener?.invoke(v, menuItem)
                    } else {
                        onItemReselectedListener?.invoke(v, menuItem)
                    }
                }
                .build(this)

        if (selectedItemId == menuItemDescriptor.itemId) {
            menuItem.select()
        }

        return menuItem
    }

    private fun onItemSelected(activeMenuItem: MenuItem) {
        if (selectedItemId == activeMenuItem.id) {
            return
        }

        delayTransition(duration = transitionDuration.toLong())

        val set = ConstraintSet()
        set.clone(this)

        menuItems.getValue(activeMenuItem.id).select()
        menuItems.getValue(selectedItemId).deselect()
        selectedItemId = activeMenuItem.id

        set.applyTo(this)
    }

    internal class ExpandableBottomBarStateController(
        private val expandableBottomBar: ExpandableBottomBar
    ) {

        fun store(superState: Parcelable?) = BottomBarSavedState(expandableBottomBar.selectedItemId, superState)

        fun restore(stateBottomBar: BottomBarSavedState) {
            val selectedItemId = stateBottomBar.selectedItem
            val menuItem = expandableBottomBar.menuItems.getValue(selectedItemId)
            expandableBottomBar.onItemSelected(menuItem)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class ExpandableBottomBarOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(bounds, clamp(backgroundCornerRadius, 0F, min(height, width) / 2F))
        }
    }
}
