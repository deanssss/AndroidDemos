package xyz.dean.androiddemos.utils.prefrences

fun PrefModel.intFiled(key: String? = null, default: Int = -1) = IntPrefField(key, default)
fun PrefModel.nullableStringFiled(key: String? = null, default: String? = null) = StringPrefFiled(key, default)
fun PrefModel.noNullStringFiled(key: String? = null, default: String = "") = StringPrefFiled(key, default)