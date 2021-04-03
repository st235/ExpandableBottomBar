package github.com.st235.lib_expandablebottombar

import android.animation.Animator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar.ItemStyle.Companion.toItemStyle
import github.com.st235.lib_expandablebottombar.behavior.ExpandableBottomBarBehavior
import github.com.st235.lib_expandablebottombar.parsers.ExpandableBottomBarParser
import github.com.st235.lib_expandablebottombar.state.BottomBarSavedState
import github.com.st235.lib_expandablebottombar.utils.*

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

    @FloatRange(from = 0.0, to = 1.0) private val itemBackgroundOpacity: Float
    @FloatRange(from = 0.0) private val itemBackgroundCornerRadius: Float
    @IntRange(from = 0) private val menuItemHorizontalMargin: Int
    @IntRange(from = 0) private val menuItemVerticalMargin: Int
    @IntRange(from = 0) private val menuHorizontalPadding: Int
    @IntRange(from = 0) private val menuVerticalPadding: Int
    @ColorInt private val itemInactiveColor: Int
    @ColorInt private val globalBadgeColor: Int
    @ColorInt private val globalBadgeTextColor: Int
    private val transitionDuration: Int
    private val menuImpl: MenuImpl
    private val styleController: StyleController
    private val stateController = ExpandableBottomBarStateController(this)


    @FloatRange(from = 0.0) private var backgroundCornerRadius: Float = 0F
    set(value) {
        field = value
        applyForApiLAndHigher {
            invalidateOutline()
        }
    }

    val menu: Menu
    get() {
        return menuImpl
    }

    var onItemSelectedListener: OnItemClickListener?
    get() {
        return menu.onItemSelectedListener
    }
    set(value) {
        menu.onItemSelectedListener = value
    }

    var onItemReselectedListener: OnItemClickListener?
    get() {
        return menu.onItemReselectedListener
    }
    set(value) {
        menu.onItemReselectedListener = value
    }

    private var animator: Animator? = null

    init {
        if (id == View.NO_ID) {
            id = View.generateViewId()
        }

        contentDescription = resources.getString(R.string.accessibility_description)

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ExpandableBottomBar,
            defStyleAttr,
            R.style.ExpandableBottomBar
        )

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

        val menuItemFactory = MenuItemFactory(
            this,
            styleController,
            menuVerticalPadding,
            menuHorizontalPadding,
            itemBackgroundCornerRadius,
            itemBackgroundOpacity,
            itemInactiveColor, globalBadgeColor,
            globalBadgeTextColor
        )
        menuImpl = MenuImpl(this, menuItemFactory, menuItemHorizontalMargin, menuItemVerticalMargin, transitionDuration.toLong())

        val menuId = typedArray.getResourceId(R.styleable.ExpandableBottomBar_exb_items, View.NO_ID)
        if (menuId != View.NO_ID) {
            val barParser = ExpandableBottomBarParser(context)
            val items = barParser.inflate(menuId)
            for (item in items) {
                menuImpl.add(item)
            }
        }

        typedArray.recycle()
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> =
        ExpandableBottomBarBehavior<ExpandableBottomBar>()

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

    internal class ExpandableBottomBarStateController(
        private val expandableBottomBar: ExpandableBottomBar
    ) {

        fun store(superState: Parcelable?): Parcelable {
            val selectedItem = expandableBottomBar.menu.selectedItem
            return BottomBarSavedState(selectedItem?.id, superState)
        }

        fun restore(stateBottomBar: BottomBarSavedState) {
            val selectedItemId = stateBottomBar.selectedItem

            expandableBottomBar.menu.doSilently { menu ->
                if (selectedItemId != null) {
                    try {
                        menu.select(selectedItemId)
                    } catch (e: IllegalArgumentException) {
                        // catch exception here as it is possible that
                        // menu item do not exists and should be added later
                        menu.deselect()
                    }
                }
            }
        }

        private inline fun Menu.doSilently(scope: (menu: Menu) -> Unit) {
            val selectedItemListener = onItemSelectedListener
            val reselectedItemListener = onItemReselectedListener
            onItemSelectedListener = null
            onItemReselectedListener = null
            scope(this)
            onItemSelectedListener = selectedItemListener
            onItemReselectedListener = reselectedItemListener
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class ExpandableBottomBarOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(bounds, clamp(backgroundCornerRadius, 0F, min(height, width) / 2F))
        }
    }
}
