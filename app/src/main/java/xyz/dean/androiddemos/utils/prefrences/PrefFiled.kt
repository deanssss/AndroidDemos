package xyz.dean.androiddemos.utils.prefrences

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class PrefFiled<T>
internal constructor(
        val key: String? = null,
        val default: T,
        val useName: Boolean = !key.isNullOrBlank()
) : ReadWriteProperty<Any?, T> {

    abstract fun getValue(property: KProperty<*>, prefModel: PrefModel): T
    abstract fun setValue(property: KProperty<*>, prefModel: PrefModel, value: T)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue(property, thisRef as PrefModel)
    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setValue(property, thisRef as PrefModel, value)
}

class IntPrefField
internal constructor(
        key: String?, default: Int
) : PrefFiled<Int>(key, default) {

    override fun getValue(property: KProperty<*>, prefModel: PrefModel): Int {
        val pref = prefModel.getPreference()
        val key = if (useName) key else property.name

        return pref.getInt(key, default)
    }

    override fun setValue(property: KProperty<*>, prefModel: PrefModel, value: Int) {
        val editor = prefModel.getPreference().edit()
        val key = if (useName) key else property.name

        return editor.putInt(key, value).apply()
    }
}

class StringPrefFiled
internal constructor(
        key: String?, default: String?
) : PrefFiled<String?>(key, default) {
    override fun getValue(property: KProperty<*>, prefModel: PrefModel): String? {
        val pref = prefModel.getPreference()
        val key = if (useName) key else property.name

        return pref.getString(key, default)
    }

    override fun setValue(property: KProperty<*>, prefModel: PrefModel, value: String?) {
        val editor = prefModel.getPreference().edit()
        val key = if (useName) key else property.name

        return editor.putString(key, value).apply()
    }
}