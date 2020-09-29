package xyz.dean.androiddemos.utils.prefrences

@Suppress("unused")
fun PrefModel.intFiled(default: Int) = IntPrefField(default).noNull(default)
@Suppress("unused")
fun PrefModel.nullableIntFiled(default: Int? = null) = IntPrefField(default)

@Suppress("unused")
fun PrefModel.stringFiled(default: String) = StringPrefFiled(default).noNull(default)
@Suppress("unused")
fun PrefModel.nullableStringFiled(default: String? = null) = StringPrefFiled(default)