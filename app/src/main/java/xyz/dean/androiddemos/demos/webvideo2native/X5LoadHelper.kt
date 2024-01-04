package xyz.dean.androiddemos.demos.webvideo2native

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View.MeasureSpec
import android.widget.FrameLayout
import android.widget.TextView
import com.tencent.smtt.export.external.embeddedwidget.interfaces.IEmbeddedWidget
import com.tencent.smtt.export.external.embeddedwidget.interfaces.IEmbeddedWidgetClient
import com.tencent.smtt.export.external.embeddedwidget.interfaces.IEmbeddedWidgetClientFactory
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebView
import xyz.dean.androiddemos.common.log
import xyz.dean.util.dp2px
import xyz.dean.util.dp2pxF

class MView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var surface: Surface? = null

    fun draw() {
        val canvas = surface?.lockCanvas(null) ?: return
        draw(canvas)
        surface?.unlockCanvasAndPost(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        log.d(TAG, "MView onTouchEvent x：${event.x} y：${event.y} action: ${when(event.action){
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_UP -> "UP"
            MotionEvent.ACTION_CANCEL -> "CANCEL"
            else -> "OTHER"
        }        }")
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        log.d(TAG, "performClick.")
        return super.performClick()
    }
}

private const val TAG = "X5LoadHelper"

object X5LoadHelper {

    fun loadX5(context: Context) {
        QbSdk.setDownloadWithoutWifi(true)
        QbSdk.initX5Environment(context, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                log.d(TAG, "on x5 core init finished.")
            }

            override fun onViewInitFinished(p0: Boolean) {
                log.d(TAG, "on x5 view init finished. use x5 core: $p0")
            }
        })
    }

    fun enableEmbedWidget(context: Context, webView: WebView) {
        if (webView.x5WebViewExtension != null){
            //强制设置EMBEDDED云控开关enable
            val tbsPublicSettings = context
                .getSharedPreferences("tbs_public_settings", Context.MODE_PRIVATE)
            val edit = tbsPublicSettings.edit()
            edit.putInt("MTT_CORE_EMBEDDED_WIDGET_ENABLE", 1)
            edit.apply()
        }else {
            log.d(TAG, "init: 非x5内核");
        }
    }

    fun registerEmbedTagHandler(context: Context, webView: WebView) {
        val result = webView.x5WebViewExtension?.registerEmbeddedWidget(arrayOf("mytag"), object : IEmbeddedWidgetClientFactory {
            override fun createWidgetClient(
                p0: String?,
                p1: MutableMap<String, String>?,
                p2: IEmbeddedWidget?
            ): IEmbeddedWidgetClient {
                log.d(TAG, "init: createWidgetClient s$p0")
                log.d(TAG, "init: createWidgetClient map${p1.toString()}")
                return VideoEmbeddedWidgetClient(context)
            }
        })
        log.d(TAG, "register embed tag handler result: $result")
    }
}

class VideoEmbeddedWidgetClient(private val context: Context) : IEmbeddedWidgetClient {

    val view = MView(context).apply {
        setBackgroundColor(Color.RED)
        TextView(context).apply {
            text = "Hello"
            textSize = context.dp2pxF(20f)
            setOnClickListener {
                log.d(TAG, "Hello click~~~~~")
            }
            addView(this)
        }
        setOnClickListener {
            log.d(TAG, "MView click~~~~~")
        }
    }
    var surface: Surface? = null

    override fun onSurfaceCreated(p0: Surface) {
        log.d(TAG, "onSurfaceCreated")
        surface = p0
        view.surface = p0
    }

    override fun onSurfaceDestroyed(p0: Surface) {
        log.d(TAG, "onSurfaceDestroyed")
        surface = null
        view.surface = null
    }

    override fun onTouchEvent(p0: MotionEvent): Boolean {
        log.d(TAG, "onTouchEvent x：${p0.x} y：${p0.y} action: ${when(p0.action){
                    MotionEvent.ACTION_DOWN -> "DOWN"
                    MotionEvent.ACTION_MOVE -> "MOVE"
                    MotionEvent.ACTION_UP -> "UP"
                    MotionEvent.ACTION_CANCEL -> "CANCEL"
                    else -> "OTHER"
        }        }")
        val result = view.dispatchTouchEvent(p0)
        log.d(TAG, "onTouchEvent result: $result ${view.width} ${view.height}")
        return result
    }

    private var lastRect: Rect? = null

    override fun onRectChanged(p0: Rect) {
        log.d(TAG, "onRectChanged ${p0.width()} ${p0.height()}")
        if (p0 != lastRect && surface != null) {
            lastRect = p0
            val width = context.dp2px(p0.width().toFloat())
            val height = context.dp2px(p0.height().toFloat())
            doTraversal(width, height)
        }
    }

    override fun onVisibilityChanged(p0: Boolean) {
        log.d(TAG, "onVisibilityChanged")
    }

    override fun onDestroy() {
        log.d(TAG, "onDestroy")
    }

    override fun onActive() {
        log.d(TAG, "onActive")
    }

    override fun onDeactive() {
        log.d(TAG, "onDeactive")
    }

    override fun onRequestRedraw() {
        log.d(TAG, "onRequestRedraw")
        view.draw()
    }

    private fun doTraversal(width: Int, height: Int) {
        view.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        )
        view.layout(0, 0, width, height)
        onRequestRedraw()
    }
}
