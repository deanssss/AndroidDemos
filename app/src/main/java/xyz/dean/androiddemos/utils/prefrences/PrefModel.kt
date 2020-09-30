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
    private val keyMap = mutableMapOf<String, String>()

    init {
        /* TODO 我知道这依然不是一个好的解决办法，但是相比通过反射获取运行时注解的方法，已经好了不少。
         *      后面我会尝试使用注解处理器在编译时解决这个问题，也许会放弃设置别名的功能。
         */
        @Suppress("LeakingThis")
        setAlias()
    }

    /**
     * Override this method and call [KProperty.alias] to set alias for model property.
     * But you should not read property's value in this method,
     * because this method is called by init block, when you read a property, it may not be initialized.
     */
    open fun setAlias() { }

    protected infix fun KProperty<*>.alias(alias: String) {
        keyMap[this.name] = alias
    }

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
        val key = getKey(property)
        val editor = preferences.edit().remove(key)

        if (isCommit) editor.apply()
        else editor.commit()
    }

    internal fun getKey(property: KProperty<*>): String {
        return keyMap[property.name] ?: property.name
    }
}