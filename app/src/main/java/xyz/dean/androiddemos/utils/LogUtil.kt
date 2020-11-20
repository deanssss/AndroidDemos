@file:Suppress("unused")

package xyz.dean.androiddemos.utils

import androidx.annotation.IntRange
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//<editor-fold desc="Log Printer for Java Console." defaultstate="collapsed">
val jlogPrinter = object : LogPrinter() {
    override fun printLog(logType: LogType, tag: String, msg: String, tr: Throwable?) {
        val time = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.CHINA).format(Date())
        val trId = String.format("%10d", Thread.currentThread().id)
        val trName = String.format("%-15s", Thread.currentThread().name)
        val typeStr = String.format("%-8s", logType.name)
        val tagStr = String.format("%15s", tag)
        val msgStr = formatMsg(msg, tr)

        val logTxt = when (logType) {
            LogType.VERBOSE ->  String.format("%s %s %s/%s [%s]: %s", time, typeStr, trId, trName, tagStr, msgStr)
            LogType.DEBUG ->    String.format("%s %s %s/%s [%s]: %s", time, cyanText(typeStr), trId, trName, cyanText(tagStr), cyanText(msgStr))
            LogType.INFO ->     String.format("%s %s %s/%s [%s]: %s", time, greenText(typeStr), trId, trName, greenText(tagStr), greenText(msgStr))
            LogType.WARNING ->  String.format("%s %s %s/%s [%s]: %s", time, yellowText(typeStr), trId, trName, yellowText(tagStr), yellowText(msgStr))
            LogType.ERROR ->    String.format("%s %s %s/%s [%s]: %s", time, redText(typeStr), trId, trName, redText(tagStr), redText(msgStr))
            LogType.ASSERT ->   String.format("%s %s %s/%s [%s]: %s", time, magentaText(typeStr), trId, trName, magentaText(tagStr), magentaText(msgStr))
        }
        print(logTxt)
    }

    private fun formatMsg(msg: String, tr: Throwable? = null): String {
        val parts = msg.split("\n")
        val sb = StringBuilder(parts[0] + "\n")
        val formattedBlank = String.format("%79s", " ")
        parts.drop(1).forEach {
            sb.append(formattedBlank).append("$it\n")
        }
        if (tr != null) {
            sb.append(formattedBlank).append("-----------\n")
                .append(formattedBlank).append("[Throw]: $tr\n")
            formatThrowable(sb, tr)
        }
        return sb.toString()
    }

    private fun formatThrowable(sb: StringBuilder, tr: Throwable) {
        val formattedBlank = String.format("%79s", " ")
        tr.stackTrace.forEach {
            sb.append(formattedBlank).append("    at $it\n")
        }
        tr.cause?.let {
            sb.append(formattedBlank).append("[Cause]: $it\n")
            formatThrowable(sb, it)
        }
    }

    private fun cyanText(text: String) = "\u001B[36m$text\u001B[0m"
    private fun greenText(text: String) = "\u001B[32m$text\u001B[0m"
    private fun yellowText(text: String) = "\u001B[33m$text\u001B[0m"
    private fun redText(text: String) = "\u001B[31m$text\u001B[0m"
    private fun magentaText(text: String) = "\u001B[35m$text\u001B[0m"
}
//</editor-fold>

class Log {
    var printers: MutableList<LogPrinter> = mutableListOf()
    var tagFilter: (String) -> Boolean = { true }

    fun v(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.VERBOSE, tag, msg.invoke(), tr)

    fun v(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.VERBOSE, tag, msg, tr)

    fun d(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.DEBUG, tag, msg.invoke(), tr)

    fun d(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.DEBUG, tag, msg, tr)

    fun i(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.INFO, tag, msg.invoke(), tr)

    fun i(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.INFO, tag, msg, tr)

    fun w(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.WARNING, tag, msg.invoke(), tr)

    fun w(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.WARNING, tag, msg, tr)

    fun e(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.ERROR, tag, msg.invoke(), tr)

    fun e(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.ERROR, tag, msg, tr)

    fun wtf(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.ASSERT, tag, msg.invoke(), tr)

    fun wtf(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.ASSERT, tag, msg, tr)

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

//<editor-fold desc="Test Log Util" defaultstate="collapsed">
fun main() {
    val log = Log()

    jlogPrinter.setLoggable(0b0011_1111)
    log.printers.add(jlogPrinter)
    log.tagFilter = { it != "Deab" }

    log.v("Dean", "Verbose log msg.")
    log.d("Dean", "Debug log msg.")
    log.i("Dean", "Info log msg.")
    log.w("Deab", "Warning log msg.")
    log.e("Dean", "Error log msg.")
    log.wtf("Dean", "WTF? log msg.")

    Thread {
        log.d("Dean", "Thread log msg.")
        Thread {
            log.d("Dean", "Child thread log msg.")
        }.start()
    }.start()

    log.i("Multiline Log", "First line.\nSecond line. \nThird line.")
    log.e("Log throwable", "Some wrongs happened.\nExceptions bellow:", Exception("Outer exception.", IOException("Inner exception", Exception("Native exception."))))
}
//</editor-fold>