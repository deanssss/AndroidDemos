package xyz.dean.androiddemos

import com.google.auto.service.AutoService
import xyz.dean.framework.common.BaseApp
import xyz.dean.framework.common.event.ComponentEventManifest
import xyz.dean.framework.common.service.ComponentServiceManifest

@Suppress("unused")
@AutoService(BaseApp::class)
class MainApp : BaseApp {
    override fun getPriority(): Int = 0

    override fun getComponentServices(): List<ComponentServiceManifest> {
        return listOf()
    }

    override fun getComponentEvents(): List<ComponentEventManifest> {
        return listOf()
    }

    override fun initModuleData() {
    }
}