package xyz.dean.androiddemos.monitor.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Process
import java.io.File

private val VSS_REGEX = "VmSize:\\s*(\\d+)\\s*kB".toRegex()
private val RSS_REGEX = "VmRSS:\\s*(\\d+)\\s*kB".toRegex()
private val THREADS_REGEX = "Threads:\\s*(\\d+)\\s*".toRegex()

private val MEM_TOTAL_REGEX = "MemTotal:\\s*(\\d+)\\s*kB".toRegex()
private val MEM_FREE_REGEX = "MemFree:\\s*(\\d+)\\s*kB".toRegex()
private val MEM_AVA_REGEX = "MemAvailable:\\s*(\\d+)\\s*kB".toRegex()
private val MEM_CMA_REGEX = "CmaTotal:\\s*(\\d+)\\s*kB".toRegex()
private val MEM_ION_REGEX = "ION_heap:\\s*(\\d+)\\s*kB".toRegex()

@JvmField
var lastProcessStatus = ProcessStatus()
@JvmField
var lastMemInfo = MemInfo()
@JvmField
var lastJavaHeap = JavaHeap()

fun getMemoryInfo(context: Context): MemoryInfo {
    //堆内存数据
    val javaHeap = getJavaHeap()
    val javaHeapMaxMem = javaHeap.max / 1024      // kb
    val javaHeapUsedMem = javaHeap.used / 1024
    val javaUsedRadio = javaHeapUsedMem * 100 / javaHeapMaxMem

    //获取手机内存信息
    val deviceInfo = getDeviceMemoryInfo()
    val deviceAvailableMem = deviceInfo.availableInKb

    //进程内存数据 pss
    val processMemoryInfo = getProcessMemoryInfo(context)
    val totalpss = processMemoryInfo.totalPss
    val javapss = processMemoryInfo.dalvikPss
    val nativepss = if (processMemoryInfo.nativePss <= 0)
        Debug.getNativeHeapAllocatedSize().toInt() else processMemoryInfo.nativePss
    val processStatus = getProcessStatus()
    val rss = processStatus.rssKbSize
    val vss = processStatus.vssKbSize
    val threadsCount = processStatus.threadsCount

    //系统内存数据
    val systemMemoryInfo = ActivityManager.MemoryInfo().apply {
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .also { it.getMemoryInfo(this) }
    }
    val systemAvailabeMem = systemMemoryInfo.availMem / 1024
    val maxNativeMemory = if (systemAvailabeMem <= 0) {
        systemAvailabeMem
    } else {
        deviceAvailableMem
    }

    return MemoryInfo(
        javaHeapMaxMem = javaHeapMaxMem / 1024,
        javaHeapUsedMem = javaHeapUsedMem / 1024,
        javaUsedRadio = javaUsedRadio,
        javaPss = javapss / 1024,
        nativePss = nativepss / 1024,
        maxNativeMemory = maxNativeMemory / 1024,
        totalPss = totalpss / 1024,
        rss = rss / 1024,
        vss = vss / 1024,
        threadsCount = threadsCount
    )
}

/**
 * Get Pss/Vss/etc.
 */
fun getProcessStatus() = File("/proc/self/status")
    .runCatching {
        useLines {
            val processStatus = it.fold(ProcessStatus()) { acc, line ->
                when {
                    line.startsWith("VmSize") -> acc.vssKbSize = VSS_REGEX.matchValue(line)
                    line.startsWith("VmRSS") -> acc.rssKbSize = RSS_REGEX.matchValue(line)
                    line.startsWith("Threads") -> acc.threadsCount = THREADS_REGEX.matchValue(line)
                }
                acc
            }
            lastProcessStatus = processStatus
            return@useLines processStatus
        }
    }
    .getOrDefault(ProcessStatus())

fun getProcessMemoryInfo(context: Context): Debug.MemoryInfo {
    val activityManager = context.applicationContext
        ?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    return activityManager?.getProcessMemoryInfo(intArrayOf(Process.myPid()))
        ?.firstOrNull()
        ?: Debug.MemoryInfo().apply { Debug.getMemoryInfo(this) }
}

/**
 * 获取手机内存信息
 */
fun getDeviceMemoryInfo() = File("/proc/meminfo")
    .runCatching {
        useLines {
            val memInfo = it.fold(MemInfo()) { acc, line ->
                when {
                    line.startsWith("MemTotal") -> acc.totalInKb = MEM_TOTAL_REGEX.matchValue(line)
                    line.startsWith("MemFree") -> acc.freeInKb = MEM_FREE_REGEX.matchValue(line)
                    line.startsWith("MemAvailable") -> acc.availableInKb =
                        MEM_AVA_REGEX.matchValue(line)
                    line.startsWith("CmaTotal") -> acc.cmaTotal = MEM_CMA_REGEX.matchValue(line)
                    line.startsWith("ION_heap") -> acc.IONHeap = MEM_ION_REGEX.matchValue(line)
                }
                acc
            }
            memInfo.rate = 1.0f * memInfo.availableInKb / memInfo.totalInKb
            lastMemInfo = memInfo
            return@useLines memInfo
        }
    }
    .getOrDefault(MemInfo())

/**
 * 获取堆内存信息
 */
fun getJavaHeap(): JavaHeap {
    return JavaHeap(
        max = Runtime.getRuntime().maxMemory(),
        total = Runtime.getRuntime().totalMemory(),
        free = Runtime.getRuntime().freeMemory()
    ).also {
        lastJavaHeap = it
    }
}

private fun Regex.matchValue(s: String) =
    matchEntire(s.trim())?.groupValues?.getOrNull(1)?.toLong() ?: 0L

data class ProcessStatus(
    var vssKbSize: Long = 0,
    var rssKbSize: Long = 0,
    var threadsCount: Long = 0
)

data class MemInfo(
    var totalInKb: Long = 0,
    var freeInKb: Long = 0,
    var availableInKb: Long = 0,
    var IONHeap: Long = 0,
    var cmaTotal: Long = 0,
    var rate: Float = 0f
)

data class JavaHeap(
    val max: Long = 0,
    val total: Long = 0,
    val free: Long = 0

) {
    val used: Long = this.total - this.free
}