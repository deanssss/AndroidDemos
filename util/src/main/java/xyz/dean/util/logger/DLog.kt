@file:Suppress("unused")

package xyz.dean.util.logger

import androidx.annotation.IntRange

interface ILog {
    fun v(tag: String, tr: Throwable? = null, msg: () -> String)
    fun v(tag: String, msg: String, tr: Throwable? = null)
    fun d(tag: String, tr: Throwable? = null, msg: () -> String)
    fun d(tag: String, msg: String, tr: Throwable? = null)
    fun i(tag: String, tr: Throwable? = null, msg: () -> String)
    fun i(tag: String, msg: String, tr: Throwable? = null)
    fun w(tag: String, tr: Throwable? = null, msg: () -> String)
    fun w(tag: String, msg: String, tr: Throwable? = null)
    fun e(tag: String, tr: Throwable? = null, msg: () -> String)
    fun e(tag: String, msg: String, tr: Throwable? = null)
    fun wtf(tag: String, tr: Throwable? = null, msg: () -> String)
    fun wtf(tag: String, msg: String, tr: Throwable? = null)
}

class DLog : ILog {
    var printers: MutableList<LogPrinter> = mutableListOf()
    var tagFilter: (String) -> Boolean = { true }

    override fun v(tag: String, tr: Throwable?, msg: () -> String) =
        log(LogPrinter.LogType.VERBOSE, tag, msg.invoke(), tr)

    override fun v(tag: String, msg: String, tr: Throwable?) =
        log(LogPrinter.LogType.VERBOSE, tag, msg, tr)

    override fun d(tag: String, tr: Throwable?, msg: () -> String) =
        log(LogPrinter.LogType.DEBUG, tag, msg.invoke(), tr)

    override fun d(tag: String, msg: String, tr: Throwable?) =
        log(LogPrinter.LogType.DEBUG, tag, msg, tr)

    override fun i(tag: String, tr: Throwable?, msg: () -> String) =
        log(LogPrinter.LogType.INFO, tag, msg.invoke(), tr)

    override fun i(tag: String, msg: String, tr: Throwable?) =
        log(LogPrinter.LogType.INFO, tag, msg, tr)

    override fun w(tag: String, tr: Throwable?, msg: () -> String) =
        log(LogPrinter.LogType.WARNING, tag, msg.invoke(), tr)

    override fun w(tag: String, msg: String, tr: Throwable?) =
        log(LogPrinter.LogType.WARNING, tag, msg, tr)

    override fun e(tag: String, tr: Throwable?, msg: () -> String) =
        log(LogPrinter.LogType.ERROR, tag, msg.invoke(), tr)

    override fun e(tag: String, msg: String, tr: Throwable?) =
        log(LogPrinter.LogType.ERROR, tag, msg, tr)

    override fun wtf(tag: String, tr: Throwable?, msg: () -> String) =
        log(LogPrinter.LogType.ASSERT, tag, msg.invoke(), tr)

    override fun wtf(tag: String, msg: String, tr: Throwable? ) =
        log(LogPrinter.LogType.ASSERT, tag, msg, tr)

    private fun log(type: LogPrinter.LogType, tag: String, msg: String, tr: Throwable? = null) {
        printers.forEach { printer ->
            if (printer.shouldLog(type) && tagFilter.invoke(tag)) {
                printer.printLog(type, tag, msg, tr)
            }
        }
    }
}

abstract class LogPrinter {
    @IntRange(from = 0b0000_0000, to = 0b0011_1111)
    private var loggable: Int = DEFAULT_LOGGABLE

    abstract fun printLog(logType: LogType, tag: String, msg: String, tr: Throwable? = null)

    fun shouldLog(type: LogType) = type.value and loggable != 0

    /**
     * Set what type of log can be printed.
     *
     * @param [types] is a var argument, what LogType you pass in will be allowed to print.
     */
    fun setLoggable(vararg types: LogType) {
        loggable = DISABLE_ALL
        types.forEach {
            loggable = loggable or it.value
        }
    }

    /**
     * Set what type of log can be printed.
     *
     * @param [loggable] is a 6-bit value, and each bit represents a type of Log.
     *
     * ```
     *  LogType    MatchBit
     *  -------------------
     *  Verbose     00_0001
     *  Debug       00_0010
     *  Information 00_0100
     *  Warning     00_1000
     *  Error       01_0000
     *  Assert      10_0000
     *  ```
     */
    fun setLoggable(@IntRange(from = 0b0000_0000, to = 0b0011_1111) loggable: Int) {
        this.loggable = loggable
    }

    enum class LogType(val value: Int) {
        VERBOSE (0b0000_0001),
        DEBUG   (0b0000_0010),
        INFO    (0b0000_0100),
        WARNING (0b0000_1000),
        ERROR   (0b0001_0000),
        ASSERT  (0b0010_0000)
    }

    companion object {
        /** Disable all priorities */
        const val DISABLE_ALL       = 0b0000_0000
        /** priorities > WARNING */
        const val ERROR_LOGGABLE    = 0b0011_0000
        /** priorities > INFO */
        const val DEFAULT_LOGGABLE  = 0b0011_1000
        /** priorities > VERBOSE */
        const val DEBUG_LOGGABLE    = 0b0011_1110
        /** All priorities */
        const val ALL_LOGGABLE      = 0b0011_1111
    }
}