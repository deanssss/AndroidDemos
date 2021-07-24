package xyz.dean.androiddemos.model

import android.content.Context
import xyz.dean.androiddemos.MyApplication
import xyz.dean.util.pref.PrefModel
import xyz.dean.util.pref.stringField

private const val SP_NAME_MAIN = "main"

object MainPrefModel : PrefModel(
    SP_NAME_MAIN, Context.MODE_PRIVATE, { MyApplication.appContext }
) {
    var last: String by stringField(default = "")

    override fun setAlias() {
        ::last alias "first-load"
    }
}