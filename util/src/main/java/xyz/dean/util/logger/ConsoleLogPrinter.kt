package xyz.dean.util.logger

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

val consoleLogPrinter = object : LogPrinter() {
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

//<editor-fold desc="Test Log Util" defaultstate="collapsed">
fun main() {
    val log = DLog()

    consoleLogPrinter.setLoggable(0b0011_1111)
    log.printers.add(consoleLogPrinter)
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