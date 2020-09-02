package xyz.dean.androiddemos.utils.prefrences

import android.content.Context
import android.content.SharedPreferences

abstract class PrefModel(
        private val fileName: String,
        private val mode: Int = Context.MODE_PRIVATE,
        private val contextProvider: () -> Context
) {
    fun getPreference(): SharedPreferences {
        return contextProvider().getSharedPreferences(fileName, mode)
    }
}