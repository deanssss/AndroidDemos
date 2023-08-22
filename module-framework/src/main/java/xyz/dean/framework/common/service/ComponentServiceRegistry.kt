@file:Suppress("unused")

package xyz.dean.framework.common.service

import xyz.dean.framework.common.annotation.Overridable
import xyz.dean.framework.common.util.LogUtil
import java.util.concurrent.ConcurrentHashMap

object ComponentServiceRegistry {
    private const val TAG = "ComponentServiceRegistry"

    @JvmStatic
    private val cachedService =
        ConcurrentHashMap<Class<out IComponentService>, Class<out IComponentService>>()

    @JvmStatic
    fun registerComponentService(
        serviceClass: Class<out IComponentService>,
        implServiceClass: Class<out IComponentService>
    ) {
        cachedService[serviceClass]?.let {
            if (it.getAnnotation(Overridable::class.java)?.value == true) {
                LogUtil.d(TAG, "service is not allowed to override, return")
                return
            }
        }
        cachedService[serviceClass] = implServiceClass
    }

    @JvmStatic
    fun getComponentService(serviceClass: Class<out IComponentService>): Class<out IComponentService>? {
        return cachedService[serviceClass]
    }

    @JvmStatic
    fun unregisterComponentService(serviceClass: Class<out IComponentService?>) {
        cachedService.remove(serviceClass)
    }
}