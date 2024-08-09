package xyz.dean.util.pref

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

abstract class PrefModel(
    private val fileName: String,
    private val mode: Int = Context.MODE_PRIVATE,
    private val contextProvider: () -> Context
) {
    private val keyMap = mutableMapOf<String, String>()

    protected fun <T, F : PrefField<T>> F.alias(
        property: KProperty<*>, alias: String
    ): F = apply {
        if (alias.isEmpty()) {
            error("Cannot set a empty preference key. property name: ${property.name}")
        }
        keyMap[property.name] = alias
    }

    val preferences: SharedPreferences by lazy {
        contextProvider().getSharedPreferences(fileName, mode)
    }

    @SuppressLint("ApplySharedPref")
    fun clear(isCommit: Boolean = false) {
        val editor = preferences.edit().clear()

        if (isCommit) editor.apply()
        else editor.commit()
    }

    /**
     * Call this method to remove the SharedPreferences data,
     * The non-null Pref property cannot be removed by assign to null,
     * but you can use this method to remove it.
     */
    @SuppressLint("ApplySharedPref")
    fun remove(property: KProperty<*>, isCommit: Boolean = false) {
        val key = getKey(property)
        val editor = preferences.edit().remove(key)

        if (isCommit) editor.apply()
        else editor.commit()
    }

    internal fun getKey(property: KProperty<*>): String {
        return keyMap[property.name] ?: property.name
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> PrefField<T?>.noNull(default: T): PrefField<T> {
        this.default = default
        return this as PrefField<T>
    }
}