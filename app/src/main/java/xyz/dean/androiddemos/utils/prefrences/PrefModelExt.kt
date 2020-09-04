package xyz.dean.androiddemos.utils.prefrences

@Suppress("unused")
fun PrefModel.intFiled(key: String? = null, default: Int) = IntPrefField(key, default).noNull(default)
@Suppress("unused")
fun PrefModel.nullableIntFiled(key: String? = null, default: Int? = null) = IntPrefField(key, default)

@Suppress("unused")
fun PrefModel.stringFiled(key: String? = null, default: String) = StringPrefFiled(key, default).noNull(default)
@Suppress("unused")
fun PrefModel.nullableStringFiled(key: String? = null, default: String? = null) = StringPrefFiled(key, default)