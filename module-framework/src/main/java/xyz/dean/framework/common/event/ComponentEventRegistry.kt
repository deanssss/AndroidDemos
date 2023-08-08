@file:Suppress("unused")

package xyz.dean.framework.common.event

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap

object ComponentEventRegistry {
    @JvmStatic
    private val eventMap: MutableMap<Class<out IComponentEvent>, MutableList<IComponentEvent>> =
        ConcurrentHashMap()

    @Synchronized
    @JvmStatic
    fun registerEventHandler(
        clazz: Class<out IComponentEvent>,
        handler: IComponentEvent
    ) {
        val handlers = eventMap[clazz] ?: mutableListOf()
        if (!handlers.contains(handler)) {
            handlers.add(handler)
        }
        eventMap[clazz] = handlers
    }

    @Synchronized
    @JvmStatic
    fun registerEventHandlerWithLifecycle(
        lifecycle: Lifecycle, clazz: Class<out IComponentEvent>,
        handler: IComponentEvent
    ) {
        registerEventHandler(clazz, handler)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                unregisterEventHandler(clazz, handler)
            }
        })
    }

    @Synchronized
    @JvmStatic
    fun unregisterEventHandler(
        clazz: Class<out IComponentEvent>,
        handler: IComponentEvent
    ) {
        eventMap[clazz]?.remove(handler)
    }

    @JvmStatic
    fun findEventHandler(clazz: Class<out IComponentEvent>): MutableList<IComponentEvent>? {
        return eventMap[clazz]
    }
}