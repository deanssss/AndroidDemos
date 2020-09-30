package xyz.dean.androiddemos.utils.prefrences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlin.reflect.KProperty

abstract class PrefFiled<T>
internal constructor(
        protected var default: T,
        private val useCommit: Boolean = false,
        private val writer: Editor.(String, T?) -> Editor,
        private val reader: SharedPreferences.(String) -> T?
) {
    private lateinit var cachedKey: String

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        thisRef as PrefModel
        val pref = thisRef.getPreference()
        if (!::cachedKey.isInitialized)
            cachedKey = thisRef.getKey(property)

        return pref.reader(cachedKey) ?: default
    }

    @SuppressLint("ApplySharedPref")
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        thisRef as PrefModel
        val editor = thisRef.getPreference().edit()
        if (!::cachedKey.isInitialized)
            cachedKey = thisRef.getKey(property)

        editor.writer(cachedKey, value)

        if (useCommit) editor.commit()
        else editor.apply()
    }
}

class IntPrefField
constructor(default: Int?) : PrefFiled<Int?>(
        default,
        writer = { k, v -> if (v != null) putInt(k, v) else remove(k) },
        reader = { k -> if (contains(k)) getInt(k, 0) else null }
) {
    @Suppress("UNCHECKED_CAST")
    internal fun noNull(default: Int): PrefFiled<Int> {
        this.default = default
        return this as PrefFiled<Int>
    }
}

class StringPrefFiled
internal constructor(default: String?) : PrefFiled<String?>(
        default,
        writer = { k, v -> if (v != null ) putString(k, v) else remove(k) },
        reader = { k -> getString(k, null) }
) {
    @Suppress("UNCHECKED_CAST")
    internal fun noNull(default: String): PrefFiled<String> {
        this.default = default
        return this as PrefFiled<String>
    }
}