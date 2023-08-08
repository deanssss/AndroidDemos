package xyz.dean.framework.common

import android.app.Application
import android.content.Context
import xyz.dean.framework.common.service.ComponentServiceRegistry
import xyz.dean.framework.common.event.ComponentEventRegistry
import java.util.*

object GlobalApplicationAgent {
    lateinit var application: Application
        private set
    private val apps = ServiceLoader
        .load(BaseApp::class.java, this::class.java.classLoader)
    private val lifecycleApps = ServiceLoader
        .load(BaseLifecycleApp::class.java, this::class.java.classLoader)

    fun init(application: Application) {
        this.application = application
        (apps + lifecycleApps)
            .sortedWith { o1, o2 -> o1.getPriority() - o2.getPriority() }
            .forEach {
                initBaseApp(it)
            }
    }

    private fun initBaseApp(app: BaseApp) {
        val services = app.getComponentServices()
        services.forEach {
            ComponentServiceRegistry.registerComponentService(it.serviceClass, it.serviceImplClass)
        }
        val events = app.getComponentEvents()
        events.forEach {
            ComponentEventRegistry.registerEventHandler(it.eventClass, it.handler)
        }
        app.initModuleData()
    }

    fun onCreated(application: Application) {
        lifecycleApps.forEach { it.onAppCreated(application) }
    }

    fun onBaseContextAttached(context: Context) {
        lifecycleApps.forEach { it.onBaseContextAttached(context) }
    }

    fun onLowMemory() {
        lifecycleApps.forEach { it.onLowMemory() }
    }

    fun onTrimMemory(level: Int) {
        lifecycleApps.forEach { it.onTrimMemory(level) }
    }
}