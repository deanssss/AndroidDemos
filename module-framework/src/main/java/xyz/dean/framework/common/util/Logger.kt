package xyz.dean.framework.common.util

@Suppress("unused")
object LogUtil {
    var logger: Logger? = null

    fun v(tag: String, msg: String, tr: Throwable? = null) {
        logger?.v(tag, msg, tr)
    }
    fun d(tag: String, msg: String, tr: Throwable? = null) {
        logger?.d(tag, msg, tr)
    }
    fun i(tag: String, msg: String, tr: Throwable? = null) {
        logger?.i(tag, msg, tr)
    }
    fun w(tag: String, msg: String, tr: Throwable? = null) {
        logger?.w(tag, msg, tr)
    }
    fun e(tag: String, msg: String, tr: Throwable? = null) {
        logger?.e(tag, msg, tr)
    }
    fun wtf(tag: String, msg: String, tr: Throwable? = null) {
        logger?.wtf(tag, msg, tr)
    }
}

interface Logger {
    fun v(tag: String, msg: String, tr: Throwable?)
    fun d(tag: String, msg: String, tr: Throwable?)
    fun i(tag: String, msg: String, tr: Throwable?)
    fun w(tag: String, msg: String, tr: Throwable?)
    fun e(tag: String, msg: String, tr: Throwable?)
    fun wtf(tag: String, msg: String, tr: Throwable?)
}