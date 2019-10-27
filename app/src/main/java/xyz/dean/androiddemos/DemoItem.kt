package xyz.dean.androiddemos

import androidx.annotation.DrawableRes

val demos = mutableMapOf<String, DemoItem>()

fun addDemo(demo: DemoItem) {
    demos[demo.key] = demo
}

data class DemoItem(
    val key: String,
    val nameRes: Int,
    val describeRes: Int,
    val clazz: Class<out BaseActivity>,
    @DrawableRes val iconRes: Int = R.mipmap.img_practice)