package xyz.dean.androiddemos.common

import xyz.dean.androiddemos.BuildConfig
import xyz.dean.androiddemos.utils.Log
import xyz.dean.androiddemos.utils.LogPrinter

/**
 * Log printer for Android.
 */
val alogPrinter = object : LogPrinter() {
    override fun printLog(logType: LogType, tag: String, msg: String, tr: Throwable?) {
        when (logType) {
            LogType.VERBOSE ->  if (tr != null) android.util.Log.v(tag, msg, tr) else android.util.Log.v(tag, msg)
            LogType.DEBUG ->    if (tr != null) android.util.Log.d(tag, msg, tr) else android.util.Log.d(tag, msg)
            LogType.INFO ->     if (tr != null) android.util.Log.i(tag, msg, tr) else android.util.Log.i(tag, msg)
            LogType.WARNING ->  if (tr != null) android.util.Log.w(tag, msg, tr) else android.util.Log.w(tag, msg)
            LogType.ERROR ->    if (tr != null) android.util.Log.e(tag, msg, tr) else android.util.Log.e(tag, msg)
            LogType.ASSERT ->   if (tr != null) android.util.Log.wtf(tag, msg, tr) else android.util.Log.wtf(tag, msg)
        }
    }
}

/**
 * Default global log utils.
 */
val log = Log().apply {
    printer = alogPrinter
    if (BuildConfig.DEBUG) {
        printer.setLoggable(LogPrinter.ALL_LOGGABLE)
    } else {
        printer.setLoggable(LogPrinter.DEFAULT_LOGGABLE)
    }
}