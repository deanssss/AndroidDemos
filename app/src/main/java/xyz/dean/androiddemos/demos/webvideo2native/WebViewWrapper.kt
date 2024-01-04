package xyz.dean.androiddemos.demos.webvideo2native

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class WebViewWrapper @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs) {
    var onScrollChangeListener: ((x: Int, y: Int, oldX: Int, oldY: Int) -> Unit)? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangeListener?.invoke(l, t, oldl, oldt)
    }
}