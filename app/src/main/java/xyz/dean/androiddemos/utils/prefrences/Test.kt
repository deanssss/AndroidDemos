package xyz.dean.androiddemos.utils.prefrences

import xyz.dean.androiddemos.MyApplication

class Test : PrefModel("Test", contextProvider = { MyApplication.appContext }) {
    val age: Int by intFiled()
//    val name: String by noNullStringFiled()
}