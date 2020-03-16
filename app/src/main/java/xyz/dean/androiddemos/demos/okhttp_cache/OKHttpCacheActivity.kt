package xyz.dean.androiddemos.demos.okhttp_cache

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_okhttp_cache.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

private typealias DInterceptor = (Interceptor.Chain) -> Response

class OKHttpCacheActivity : BaseActivity() {
    override fun getDemoItem(): DemoItem? = demoItem

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_okhttp_cache)

        val requestInterceptor: DInterceptor = {
            val newReq = it.request().newBuilder()
            if (!isNetworkConnected(this)) {
                // 离线状态将请求强制修改为从缓存中获取数据，无论缓存是否过期
                newReq.removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .cacheControl(CacheControl.FORCE_CACHE)
            }
            it.proceed(newReq.build())
        }
        val responseInterceptor: DInterceptor = {
            val newRes = it.proceed(it.request())
            if (isNetworkConnected(this))
                newRes.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    // 为保证联网状态下web能及时更新，设置缓存生命为5s即可
                    .addHeader("Cache-Control", "max-age=5")
                    .build()
            else newRes
        }

        val client = OkHttpClient.Builder()
            .cache(Cache(
                File(cacheDir, "ok-cache"),
                10 * 1024 * 1024
            ))
            .addInterceptor(requestInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS })
            .addNetworkInterceptor(responseInterceptor)
            .build()

        web.settings.apply {
            javaScriptEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            builtInZoomControls = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = true
        }
        // shouldInterceptRequest在非UIThread运行，无法获取WebView的UA，故在此获取
        val ua = web.settings.userAgentString

        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url.toString()
                if (url != "https://juejin.im/pins/recommended")
                    return super.shouldInterceptRequest(view, request)

                val requestBuilder = Request.Builder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", ua)
                    .get()
                    .url(url)

                val call = client.newCall(requestBuilder.build())
                val okResponse = call.execute()

                val type = okResponse.body()?.contentType()?.let {
                    if (!it.subtype().isNullOrBlank()) {
                        "${it.type()}/${it.subtype()}"
                    } else {
                        it.type()
                    }
                }
                val warn = (okResponse.header("Warning")
                    ?.split(" ")
                    ?.get(0) ?: "0")
                    .toInt()
                log.d("Dean", "Response Data Type: $type Code: $warn")

                var cacheMiss = false
                val (loading: String, inputStream: InputStream) = when {
                    warn == 110 -> { // 缓存存在，但是过期了
                        Log.d("Dean", "use cache")
                        "Loading web page from cache." to okResponse.body()!!.byteStream()
                    }
                    okResponse.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> { // 缓存不存在，离线强制请求时会出错
                        okResponse.body()!!.close()
                        Log.d("Dean", "no network and no cache")
                        cacheMiss = true
                        "Loading error page from local storage." to okResponse.body()!!.byteStream()
                    }
                    else -> { // 正常从网络加载
                        Log.d("Dean", "Loading from network.")
                        "Loading web page from network." to okResponse.body()!!.byteStream()
                    }
                }
                runOnUiThread {
                    Toast.makeText(this@OKHttpCacheActivity, loading, Toast.LENGTH_SHORT).show()
                    if (cacheMiss)
                        web.loadUrl("file:///android_asset/cache-error.html")
                }
                return WebResourceResponse(type, "UTF-8", inputStream)
            }
        }
        web.loadUrl("https://juejin.im/pins/recommended")
    }

    companion object {
        val demoItem = DemoItem(
            key = "okhttp-cache",
            nameRes = R.string.okhttp_cache_demo_name,
            describeRes = R.string.okhttp_cache_demo_describe,
            clazz = OKHttpCacheActivity::class.java
        )

        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni?.isConnected ?: false
        }
    }
}