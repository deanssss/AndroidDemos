package xyz.dean.framework.common.service

data class ComponentServiceManifest(
    val serviceClass: Class<out IComponentService>,
    val serviceImplClass: Class<out IComponentService>
)