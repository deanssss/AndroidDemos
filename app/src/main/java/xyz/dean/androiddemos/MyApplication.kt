package xyz.dean.androiddemos

import android.app.Application
import android.content.Context
import xyz.dean.androiddemos.demos.infinite_list.InfiniteTabActivity
import xyz.dean.androiddemos.demos.behavior.BehaviorTestActivity
import xyz.dean.androiddemos.demos.blur_image.BlurImageActivity
import xyz.dean.androiddemos.demos.dag_task.DagTaskExecActivity
import xyz.dean.androiddemos.demos.dragtreelist.DragTreeListActivity
import xyz.dean.androiddemos.demos.rating_bar.CusRatingBarDemoActivity
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
        addDemo(DragTreeListActivity.demoItem)
        addDemo(BlurImageActivity.demoItem)
        addDemo(CusRatingBarDemoActivity.demoItem)
        addDemo(DagTaskExecActivity.demoItem)
    }

    companion object {
        lateinit var appContext: Context
    }
}