package xyz.dean.androiddemos.utils.prefrences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlin.reflect.KProperty

abstract class PrefFiled<T>
internal constructor(
        private val key: String? = null,
        protected var default: T,
        private val useCommit: Boolean = false,
        private val writer: Editor.(String, T?) -> Editor,
        private val reader: SharedPreferences.(String) -> T?
) {
    private val provideKey: Boolean = !key.isNullOrBlank()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val pref = (thisRef as PrefModel).getPreference()
        val key = if (provideKey) key else property.name
        return pref.reader(key!!) ?: default
    }

    @SuppressLint("ApplySharedPref")
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val editor = (thisRef as PrefModel).getPreference().edit()
        val key = if (provideKey) key else property.name

        editor.writer(key!!, value)

        if (useCommit) editor.commit()
        else editor.apply()
    }
}

class IntPrefField
constructor(
        key: String?, default: Int?
) : PrefFiled<Int?>(
        key, default,
        writer = { k, v -> if (v != null) putInt(k, v) else remove(k) },
        reader = { k -> if (contains(k)) getInt(k, 0) else null }
) {
    internal fun noNull(default: Int): PrefFiled<Int> {
        this.default = default
        @Suppress("UNCHECKED_CAST")
        return this as PrefFiled<Int>
    }
}

class StringPrefFiled
internal constructor(
        key: String?, default: String?
) : PrefFiled<String?>(
        key, default,
        writer = { k, v -> if (v != null ) putString(k, v) else remove(k) },
        reader = { k -> getString(k, null) }
) {
    internal fun noNull(default: String): PrefFiled<String> {
        this.default = default
        @Suppress("UNCHECKED_CAST")
        return this as PrefFiled<String>
    }
}