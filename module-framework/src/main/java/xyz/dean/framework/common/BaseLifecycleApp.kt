package xyz.dean.framework.common

import android.app.Application
import android.content.Context

interface BaseLifecycleApp : BaseApp {
    fun onAppCreated(application: Application)
    fun onBaseContextAttached(context: Context)
    fun onLowMemory()
    fun onTrimMemory(level: Int)
}