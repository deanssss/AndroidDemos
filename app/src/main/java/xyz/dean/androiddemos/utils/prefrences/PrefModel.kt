package xyz.dean.androiddemos.utils.prefrences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

abstract class PrefModel(
        private val fileName: String,
        private val mode: Int = Context.MODE_PRIVATE,
        private val contextProvider: () -> Context
) {
    private val preferences: SharedPreferences by lazy {
        contextProvider().getSharedPreferences(fileName, mode)
    }

    fun getPreference() = preferences

    @SuppressLint("ApplySharedPref")
    fun clear(isCommit: Boolean = false) {
        val editor = preferences.edit().clear()

        if (isCommit) editor.apply()
        else editor.commit()
    }

    /**
     * Call this method to remove the SharedPreferences data,
     * The non-null Pref field cannot be removed by assign to null,
     * but you can use this method to remove it.
     */
    fun remove(property: KProperty<*>, isCommit: Boolean = false) {
        val key = PrefFiled.getKey(property)
        val editor = preferences.edit().remove(key)

        if (isCommit) editor.apply()
        else editor.commit()
    }
}