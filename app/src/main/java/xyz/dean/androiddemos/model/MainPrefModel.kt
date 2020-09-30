package xyz.dean.androiddemos.model

import android.content.Context
import xyz.dean.androiddemos.MyApplication
import xyz.dean.androiddemos.utils.prefrences.PrefModel
import xyz.dean.androiddemos.utils.prefrences.stringFiled

private const val SP_NAME_MAIN = "main"

object MainPrefModel : PrefModel(
    SP_NAME_MAIN, Context.MODE_PRIVATE, { MyApplication.appContext }
) {
    var last: String by stringFiled(default = "")

    override fun setAlias() {
        ::last alias "first-load"
    }
}