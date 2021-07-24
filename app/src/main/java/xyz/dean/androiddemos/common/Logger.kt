package xyz.dean.androiddemos.common

import xyz.dean.util.BuildConfig
import xyz.dean.util.logger.DLog
import xyz.dean.util.logger.LogPrinter
import xyz.dean.util.logger.androidLogPrinter

val log = DLog().apply {
    if (BuildConfig.DEBUG) {
        androidLogPrinter.setLoggable(LogPrinter.ALL_LOGGABLE)
    } else {
        androidLogPrinter.setLoggable(LogPrinter.DEFAULT_LOGGABLE)
    }
    printers.add(androidLogPrinter)
}