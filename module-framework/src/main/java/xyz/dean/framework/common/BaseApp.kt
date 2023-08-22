package xyz.dean.framework.common

import xyz.dean.framework.common.event.ComponentEventManifest
import xyz.dean.framework.common.service.ComponentServiceManifest

interface BaseApp {
    /**
     * 模块优先级，值越低，优先级越高
     */
    fun getPriority(): Int
    fun getComponentServices(): List<ComponentServiceManifest>
    fun getComponentEvents(): List<ComponentEventManifest>
    fun initModuleData()
}