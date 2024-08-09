package xyz.dean.util.pref

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlin.reflect.KProperty

abstract class PrefField<T>
internal constructor(
    var default: T,
    private val useCommit: Boolean = false,
    private val writer: Editor.(String, T?) -> Editor,
    private val reader: SharedPreferences.(String) -> T?
) {
    private var cachedKey: String = ""

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        thisRef as PrefModel
        val pref = thisRef.preferences
        if (cachedKey.isEmpty())
            cachedKey = thisRef.getKey(property)

        return pref.reader(cachedKey) ?: default
    }

    @SuppressLint("ApplySharedPref")
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        thisRef as PrefModel
        val editor = thisRef.preferences.edit()
        if (cachedKey.isEmpty())
            cachedKey = thisRef.getKey(property)

        editor.writer(cachedKey, value)

        if (useCommit) editor.commit()
        else editor.apply()
    }
}

class IntPrefField
internal constructor(default: Int?) : PrefField<Int?>(
        default,
        writer = { k, v -> if (v != null) putInt(k, v) else remove(k) },
        reader = { k -> if (contains(k)) getInt(k, 0) else null }
)

class StringPrefField
internal constructor(default: String?) : PrefField<String?>(
        default,
        writer = { k, v -> if (v != null) putString(k, v) else remove(k) },
        reader = { k -> getString(k, null) }
)

class StringSetField
internal constructor(default: Set<String>?) : PrefField<Set<String>?>(
        default,
        writer = { k, v -> if (!v.isNullOrEmpty()) putStringSet(k, v) else remove(k) },
        reader = { k -> getStringSet(k, null) }
)

class LongField
internal constructor(default: Long?) : PrefField<Long?>(
        default,
        writer = { k,v -> if (v != null) putLong(k, v) else remove(k) },
        reader = { k -> if (contains(k)) getLong(k, 0L) else null }
)

class FloatField
internal constructor(default: Float?) : PrefField<Float?>(
        default,
        writer = { k, v -> if (v != null) putFloat(k, v) else remove(k) },
        reader = { k -> if (contains(k)) getFloat(k, 0f) else null }
)

class BooleanField
internal constructor(default: Boolean?) : PrefField<Boolean?>(
        default,
        writer = { k, v -> if (v != null) putBoolean(k, v) else remove(k) },
        reader = { k -> if (contains(k)) getBoolean(k, false) else null }
)

class ObjectField<T>
internal constructor(
    default: T?,
    serializer: (T) -> String,
    parser: (String) -> T,
) : PrefField<T?>(
    default,
    writer = { k, v -> if (v != null) putString(k, serializer(v)) else remove(k) },
    reader = { k -> if (contains(k)) parser(getString(k, "")!!) else null }
)