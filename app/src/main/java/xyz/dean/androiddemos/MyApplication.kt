package xyz.dean.androiddemos

import android.app.Application
import android.content.Context
import xyz.dean.androiddemos.common.log
import xyz.dean.androiddemos.demos.behavior.BehaviorTestActivity
import xyz.dean.androiddemos.demos.blur_image.BlurImageActivity
import xyz.dean.androiddemos.demos.dag_task.DagTaskExecActivity
import xyz.dean.androiddemos.demos.dragtreelist.DragTreeListActivity
import xyz.dean.androiddemos.demos.infinite_list.InfiniteTabActivity
import xyz.dean.androiddemos.demos.memdetector.MemDetectorActivity
import xyz.dean.androiddemos.demos.rating_bar.CusRatingBarDemoActivity
import xyz.dean.androiddemos.demos.sample.SampleActivity
import xyz.dean.framework.common.GlobalApplicationAgent
import xyz.dean.framework.common.util.LogUtil
import xyz.dean.framework.common.util.Logger
import xyz.dean.util.logger.ILog

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDemos()
        appContext = this

        LogUtil.logger = object : Logger, ILog by log { }
        GlobalApplicationAgent.init(this)
        GlobalApplicationAgent.onCreated(this)
    }

    private fun initDemos() {
        addDemo(SampleActivity.demoItem)
        addDemo(InfiniteTabActivity.demoItem)
        addDemo(BehaviorTestActivity.demoItem)
        addDemo(DragTreeListActivity.demoItem)
        addDemo(BlurImageActivity.demoItem)
        addDemo(CusRatingBarDemoActivity.demoItem)
        addDemo(DagTaskExecActivity.demoItem)
        addDemo(MemDetectorActivity.demoItem)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        GlobalApplicationAgent.onBaseContextAttached(base)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        GlobalApplicationAgent.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlobalApplicationAgent.onTrimMemory(level)
    }

    companion object {
        lateinit var appContext: Context
    }
}