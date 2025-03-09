package xyz.dean.androiddemos

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import xyz.dean.util.str

val demos = mutableMapOf<String, DemoItem>()

fun addDemo(demo: DemoItem) {
    demos[demo.key] = demo
}

data class DemoItem(
    val key: String,
    val name: String,
    val describe: String,
    val clazz: Class<out BaseActivity>,
    @DrawableRes val iconRes: Int = R.mipmap.img_practice
) {
    constructor(
        key: String,
        @StringRes nameRes: Int,
        @StringRes describeRes: Int,
        clazz: Class<out BaseActivity>,
        @DrawableRes iconRes: Int = R.mipmap.img_practice
    ) : this(
        key,
        name = nameRes.str,
        describe = describeRes.str,
        clazz,
        iconRes
    )
}