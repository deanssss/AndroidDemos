package xyz.dean.androiddemos

import android.app.Application
import xyz.dean.androiddemos.demos.infinite_list.InfiniteTabActivity
import xyz.dean.androiddemos.demos.sample.SampleActivity

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDemos()
    }

    private fun initDemos() {
        addDemo(SampleActivity.demoItem)
        addDemo(InfiniteTabActivity.demoItem)
    }
}