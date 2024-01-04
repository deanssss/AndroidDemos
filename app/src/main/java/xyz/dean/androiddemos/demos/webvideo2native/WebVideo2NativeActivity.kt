package xyz.dean.androiddemos.demos.webvideo2native

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.google.gson.Gson
import com.tencent.smtt.sdk.QbSdk
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.BuildConfig
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import xyz.dean.androiddemos.databinding.ActivityWebvideo2nativeBinding
import xyz.dean.util.dp2px
import xyz.dean.util.fromJson

class WebVideo2NativeActivity : BaseActivity() {

    private lateinit var binding: ActivityWebvideo2nativeBinding

    override fun getDemoItem(): DemoItem = demoItem

    @UnstableApi @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        X5LoadHelper.loadX5(this.application)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_webvideo2native)
        binding.oepnX5.setOnClickListener {
            val intent = X5WebVideo2NativeActivity.createIntent(this)
            startActivity(intent)
        }
        binding.webview.apply {
            webChromeClient = createWebChromeClient()
            webViewClient = createWebViewClient()

            initSettings()
            val gson = Gson()
            addJavascriptInterface(JSToNative(
                onVideoDetected = {
                    val videoInfo: VideoInfo = gson.fromJson(it)
                    binding.webview.post {
                        createPlayer(videoInfo)
                        binding.realContainer.updateLayoutParams {
                            height = dp2px(videoInfo.scrollHeight.toFloat())
                        }
                        binding.widgetContainer.syncScroll(binding.webview)
                    }
                },
                onVideoPositionUpdated = { id, left, top, right, bottom ->
                }
            ), "JSToNative")

            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true)
            }

//            loadUrl("https://time.geekbang.org/course/detail/100633001-723519")
//            loadUrl("https://www.hotwoods.biz/play/42589-1-2.html")
        }
    }

    @OptIn(UnstableApi::class)
    private fun createPlayer(videoInfo: VideoInfo): PlayerView {
        return PlayerView(this@WebVideo2NativeActivity).apply {
            val exoPlayer = ExoPlayer.Builder(this@WebVideo2NativeActivity).build()
            player = exoPlayer

            exoPlayer.setMediaSource(buildMediaSource(videoInfo.url))
            exoPlayer.prepare()
            exoPlayer.play()

            val lp = FrameLayout.LayoutParams(
                dp2px(videoInfo.width.toFloat()),
                dp2px(videoInfo.height.toFloat())
            )
            lp.marginStart = dp2px(videoInfo.left.toFloat())
            lp.topMargin = dp2px(videoInfo.top.toFloat())
            binding.realContainer.addView(this, lp)
            playerCache[loadedUrl] = this
        }
    }

    @OptIn(UnstableApi::class)
    private fun buildMediaSource(videoPath: String): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

        return if (videoPath.contains(".m3u8")) {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(videoPath)))
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(videoPath)))
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
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
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

    private var loadedUrl = ""
    private var playerCache = mutableMapOf<String, PlayerView>()

    private fun createWebViewClient(): WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            log.d(tag, "onPageFinished")
            binding.webview.evaluateJavascript(WebVideo2NativeHelper.VIDEO_DETECT_SCRIPT, null)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

                playerCache.remove(loadedUrl)?.player?.apply {
                    stop()
                    release()
                }
                binding.realContainer.removeAllViews()
                loadedUrl = url ?: ""

        }
    }

    private fun createWebChromeClient(): WebChromeClient = object : WebChromeClient() {
    }

    companion object {
        val demoItem = DemoItem("web-video-to-native",
            R.string.webvideo2native_demo_name,
            R.string.webvideo2native_describe,
            WebVideo2NativeActivity::class.java,
            R.mipmap.img_practice)
    }

    class JSToNative(
        val onVideoDetected: (String) -> Unit,
        val onVideoPositionUpdated: (String, Int, Int, Int, Int) -> Unit
    ) {
        @JavascriptInterface
        fun webViewPlayVideoAtURL(data: String) {
            log.d("JSToNative", data)
            onVideoDetected(data)
        }

        @JavascriptInterface
        fun updateVideoPosition(videoId: String, left: Int, top: Int, right: Int, bottom: Int) {
            log.d("JSToNative", "$videoId $left $top $right $bottom")
            onVideoPositionUpdated(videoId, left, top, right, bottom)
        }
    }
}
