package xyz.dean.androiddemos.utils

import android.view.View

fun View.dp2px(dp: Int): Int = (context.resources.displayMetrics.density * dp + 0.5f).toInt()

fun View.sp2px(sp: Int): Int = (context.resources.displayMetrics.density * sp + 0.5f).toInt()

fun View.px2dp(px: Int): Int = (px / context.resources.displayMetrics.density + 0.5f).toInt()

fun View.px2sp(px: Int): Int = (px / context.resources.displayMetrics.density + 0.5f).toInt()
