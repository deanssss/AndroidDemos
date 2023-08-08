package xyz.dean.framework.common.event

data class ComponentEventManifest(
    val eventClass: Class<out IComponentEvent>,
    val handler: IComponentEvent
)