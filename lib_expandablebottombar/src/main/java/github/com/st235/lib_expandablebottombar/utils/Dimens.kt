package github.com.st235.lib_expandablebottombar.utils

import android.content.res.Resources
import android.support.annotation.Px
import android.util.TypedValue

/**
 * Converts values to its real pixel size
 * using system density factor
 *
 * @return value in pixels
 */
@Px
internal fun Int.toPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics).toInt()
}

/**
 * Converts values to its real pixel size
 * using system density factor
 *
 * @return value in pixels
 */
internal fun Float.toPx(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this,
        Resources.getSystem().displayMetrics)
}
