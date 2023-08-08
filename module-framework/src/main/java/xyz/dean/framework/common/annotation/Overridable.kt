package xyz.dean.framework.common.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Overridable(val value: Boolean = true)
