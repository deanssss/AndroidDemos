package xyz.dean.androiddemos.demos.webvideo2native

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.media3.common.util.UnstableApi
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.BuildConfig
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.databinding.ActivityX5webvideo2nativeBinding

class X5WebVideo2NativeActivity : BaseActivity() {

    private lateinit var binding: ActivityX5webvideo2nativeBinding

    @UnstableApi @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_x5webvideo2native)
        binding.webview.apply {
            webChromeClient = createWebChromeClient()
            webViewClient = createWebViewClient()
            X5LoadHelper.enableEmbedWidget(this@X5WebVideo2NativeActivity, this)
            X5LoadHelper.registerEmbedTagHandler(this@X5WebVideo2NativeActivity, this)

            initSettings()

            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true)
            }

            loadUrl("file:////android_asset/index.html")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.initSettings() {
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.setGeolocationEnabled(true)
        settings.allowFileAccess = true
        settings.mixedContentMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        CookieManager.getInstance().flush()

        removeJavascriptInterface("searchBoxJavaBridge_")
        // 防止开启了辅助功能的手机上使用accessibility和accessibilityTraversal这两个Java Bridge来执行远程攻击代码
        // Alias for TTS API exposed to JavaScript
        removeJavascriptInterface("accessibility")
        // Alias for traversal callback exposed to JavaScript
        removeJavascriptInterface("accessibilityTraversal")
    }

    private fun createWebViewClient(): WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }
    }

    private fun createWebChromeClient(): WebChromeClient = object : WebChromeClient() {
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, X5WebVideo2NativeActivity::class.java)
    }
}
