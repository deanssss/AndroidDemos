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

    fun remove(property: KProperty<*>, isCommit: Boolean = false) {
        val editor = preferences.edit().remove(property.name)

        if (isCommit) editor.apply()
        else editor.commit()
    }
}