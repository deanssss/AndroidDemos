package xyz.dean.androiddemos

import android.app.Application
import android.content.Context
import xyz.dean.androiddemos.demos.infinite_list.InfiniteTabActivity
import xyz.dean.androiddemos.demos.behavior.BehaviorTestActivity
import xyz.dean.androiddemos.demos.sample.SampleActivity

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDemos()
        appContext = this
    }

    private fun initDemos() {
        addDemo(SampleActivity.demoItem)
        addDemo(InfiniteTabActivity.demoItem)
        addDemo(BehaviorTestActivity.demoItem)
    }

    companion object {
        lateinit var appContext: Context
    }
}