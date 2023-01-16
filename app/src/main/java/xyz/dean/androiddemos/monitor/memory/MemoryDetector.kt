package xyz.dean.androiddemos.monitor.memory

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import xyz.dean.androiddemos.BuildConfig
import xyz.dean.androiddemos.common.log
import java.lang.ref.WeakReference

typealias MemoryInfoCallback = (info: MemoryInfo) -> Unit

class MemoryDetector(
    private val application: Application,
    private val memCollectionInterval: Long = 5 * 1000L //ms
) {
    private val callbacks: MutableSet<MemoryInfoCallback> = HashSet()

    private var thread: HandlerThread? = null
    private var handler: Handler? = null
    private var isPaused = true

    @UiThread
    fun prepare() {
        try {
            if (SHOW_MEM_LOG) registerMemoryInfoCallback { log.d(TAG, it.toString()) }

            thread = HandlerThread("mem-detector-thread").apply {
                start()
                handler = MemoryHandler(this@MemoryDetector, looper)
            }
        } catch (thr: Throwable) {
            log.e(TAG, "Prepare memory detector failed.", thr)
        }
    }

    @UiThread
    fun start() {
        if (!isPaused) return

        isPaused = false
        sendToDetect()
    }

    @UiThread
    fun pause() {
        isPaused = true
    }

    @UiThread
    fun stop() {
        try {
            thread?.quit()
            clearAllCallbacks()
        } catch (thr: Throwable) {
            log.e(TAG, "Stop memory detector failed.", thr)
        }
    }

    fun registerMemoryInfoCallback(memoryInfoCallback: MemoryInfoCallback) {
        callbacks.add(memoryInfoCallback)
    }

    fun unregisterMemoryInfoCallback(memoryInfoCallback: MemoryInfoCallback?) {
        callbacks.remove(memoryInfoCallback)
    }

    fun clearAllCallbacks() {
        callbacks.clear()
    }

    private fun sendToDetect() {
        if (isPaused) return
        handler?.sendEmptyMessageDelayed(MSG_DETECT, memCollectionInterval)
    }

    @WorkerThread
    private fun detectMemory() {
        val memoryInfo = getMemoryInfo(application)
        notifyMemoryInfoUpdated(memoryInfo)
    }

    private fun notifyMemoryInfoUpdated(memoryInfo: MemoryInfo) {
        callbacks.forEach { it.invoke(memoryInfo) }
    }

    companion object {
        private const val TAG = "MemoryDetector"
        private const val MSG_DETECT = 0x101
        private val SHOW_MEM_LOG = BuildConfig.DEBUG

        private class MemoryHandler(memoryDetector: MemoryDetector, looper: Looper) : Handler(looper) {
            private var detectorRef = WeakReference(memoryDetector)

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val detector = detectorRef.get() ?: return
                if (msg.what == MSG_DETECT) {
                    detector.detectMemory()
                    detector.sendToDetect()
                }
            }
        }
    }
}