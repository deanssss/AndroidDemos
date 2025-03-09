package xyz.dean.util

import android.app.Application

object ApplicationHolder {
    lateinit var application: Application
        private set

    fun init(application: Application) {
        this.application = application
    }
}