package xyz.dean.androiddemos.demos.webvideo2native

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.core.widget.NestedScrollView
import xyz.dean.androiddemos.common.log

class NativeWidgetContainerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {
    private var mWebView: WebView? = null
    private var isTouching = false

    fun syncScroll(webView: WebViewWrapper) {
        this.scrollY = webView.scrollY
        webView.onScrollChangeListener = { scrollX, scrollY, _, _ ->
            log.d("NativeWidgetContainerView", "scX: $scrollX, scY: $scrollY")
            this.scrollX = scrollX
            this.scrollY = scrollY
        }
        mWebView = webView
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isTouching) {
                    val mockDown = MotionEvent.obtain(ev).also { it.action = MotionEvent.ACTION_DOWN }
                    mWebView?.dispatchTouchEvent(mockDown)
                }
                isTouching = true
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                isTouching = false
            }
        }
        return mWebView?.dispatchTouchEvent(ev) ?: false
    }
}