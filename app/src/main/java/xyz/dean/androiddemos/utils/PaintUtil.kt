@file:Suppress("unused")

package xyz.dean.androiddemos.utils

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import androidx.annotation.ColorRes

fun Context.px2dpF(pxValue: Float): Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale
}

fun Context.dp2pxF(dipValue: Float): Float {
    val scale = resources.displayMetrics.density
    return dipValue * scale
}

fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun Context.dp2px(dipValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun Context.px2sp(pxValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

fun Context.sp2px(spValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

@Suppress("DEPRECATION")
fun Context.compatGetColor(@ColorRes colorRes: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(colorRes)
    } else {
        resources.getColor(colorRes)
    }
}

@Suppress("DEPRECATION")
fun Context.compatGetColorStateList(@ColorRes colorRes: Int): ColorStateList {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColorStateList(colorRes)
    } else {
        resources.getColorStateList(colorRes)
    }
}