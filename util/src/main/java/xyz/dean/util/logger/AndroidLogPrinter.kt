package xyz.dean.util.logger

import android.util.Log

val androidLogPrinter = object : LogPrinter() {
    override fun printLog(logType: LogType, tag: String, msg: String, tr: Throwable?) {
        when (logType) {
            LogType.VERBOSE ->  if (tr != null) Log.v(tag, msg, tr) else Log.v(tag, msg)
            LogType.DEBUG ->    if (tr != null) Log.d(tag, msg, tr) else Log.d(tag, msg)
            LogType.INFO ->     if (tr != null) Log.i(tag, msg, tr) else Log.i(tag, msg)
            LogType.WARNING ->  if (tr != null) Log.w(tag, msg, tr) else Log.w(tag, msg)
            LogType.ERROR ->    if (tr != null) Log.e(tag, msg, tr) else Log.e(tag, msg)
            LogType.ASSERT ->   if (tr != null) Log.wtf(tag, msg, tr) else Log.wtf(tag, msg)
        }
    }
}