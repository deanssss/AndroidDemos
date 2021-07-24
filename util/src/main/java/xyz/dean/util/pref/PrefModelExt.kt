@file:Suppress("unused")

package xyz.dean.util.pref

fun PrefModel.nullableIntField(default: Int? = null) = IntPrefField(default)
fun PrefModel.intField(default: Int) = nullableIntField().noNull(default)

fun PrefModel.nullableStringField(default: String? = null) = StringPrefField(default)
fun PrefModel.stringField(default: String) = nullableStringField().noNull(default)

fun PrefModel.nullableStringSetField(default: Set<String>? = null) = StringSetField(default)
fun PrefModel.stringSetField(default: Set<String>) = nullableStringSetField().noNull(default)

fun PrefModel.nullableLongField(default: Long? = null) = LongField(default)
fun PrefModel.longField(default: Long) = nullableLongField().noNull(default)

fun PrefModel.nullableFloatField(default: Float? = null) = FloatField(default)
fun PrefModel.floatField(default: Float) = nullableFloatField().noNull(default)

fun PrefModel.nullableBooleanField(default: Boolean? = null) = BooleanField(default)
fun PrefModel.booleanField(default: Boolean) = nullableBooleanField().noNull(default)