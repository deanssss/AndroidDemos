package xyz.dean.androiddemos.utils.prefrences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlin.reflect.KProperty

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Key(val name: String)

abstract class PrefFiled<T>
internal constructor(
        protected var default: T,
        private val useCommit: Boolean = false,
        private val writer: Editor.(String, T?) -> Editor,
        private val reader: SharedPreferences.(String) -> T?
) {
    private lateinit var cachedKey: String

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val pref = (thisRef as PrefModel).getPreference()
        if (!::cachedKey.isInitialized)
            cachedKey = getKey(property)

        return pref.reader(cachedKey) ?: default
    }

    @SuppressLint("ApplySharedPref")
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val editor = (thisRef as PrefModel).getPreference().edit()
        if (!::cachedKey.isInitialized)
            cachedKey = getKey(property)

        editor.writer(cachedKey, value)

        if (useCommit) editor.commit()
        else editor.apply()
    }

    companion object {
        internal fun getKey(property: KProperty<*>): String {
            // TODO 用反射解决了一个小问题，有点难受...
            val keyAnnotation = property.annotations.filterIsInstance<Key>().firstOrNull()
            return keyAnnotation?.name ?: property.name
        }
    }
}

class IntPrefField
constructor(default: Int?) : PrefFiled<Int?>(
        default,
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
internal constructor(default: String?) : PrefFiled<String?>(
        default,
        writer = { k, v -> if (v != null ) putString(k, v) else remove(k) },
        reader = { k -> getString(k, null) }
) {
    internal fun noNull(default: String): PrefFiled<String> {
        this.default = default
        @Suppress("UNCHECKED_CAST")
        return this as PrefFiled<String>
    }
}