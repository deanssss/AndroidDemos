package xyz.dean.androiddemos.demos.sample

import android.os.Bundle
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import xyz.dean.androiddemos.demos.vpntest.NetworkUtils

class SampleActivity : BaseActivity() {

    override fun getDemoItem(): DemoItem? = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        log.e(tag, "isVpn:" + NetworkUtils.isVpnConnected())
    }

    companion object {
        val demoItem = DemoItem("sample",
            R.string.sample_demo_name,
            R.string.sample_describe_text,
            SampleActivity::class.java, R.mipmap.img_practice)
    }
}
