package xyz.dean.androiddemos.demos.sample

import android.os.Bundle
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R

class SampleActivity : BaseActivity() {

    override fun getDemoItem(): DemoItem? = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
    }

    companion object {
        val demoItem = DemoItem("sample",
            R.string.sample_demo_name,
            R.string.sample_describe_text,
            SampleActivity::class.java, R.mipmap.img_practice)
    }
}
