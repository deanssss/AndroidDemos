package xyz.dean.androiddemos.demos.behavior

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import java.util.*
import kotlin.random.Random

/* AppBarLayout设置app:layout_scrollFlags="scroll|enterAlways"，当内容部分向上滚动时，进入（enter）的
 * AppBar会将内容视图往下顶，使其超出屏幕底部，在一些特殊的布局中，比如WebView中有依赖显示区域底部的组件时，这些组件
 * 会在AppBar进入时无法完全显示。
 * 解决思路：在AppBar进入时，逐渐改变内容视图的高度，使其不超过屏幕底部。此demo中给出两种方案:
 *      1. 使用AppBarLayout$OnOffsetChangedListener监听AppBar的offset，根据offset改变内容视图的高度。
 *      2. 自定义Behavior，建立内容视图与AppBar之间的依赖，根据AppBar的位置改变内容视图的高度。
 * 遗留问题：若内容视图中依赖底部的组件由原生实现时效果较好，如果是WebView中的web网页，则改变不会很及时，有明显的滞后感觉。
 */

class BehaviorTestActivity : BaseActivity() {
    override fun getDemoItem(): DemoItem = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_behavior_test)
        initView()
    }

    private fun initView() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.behavior_bar_layout)
        val toolbar = findViewById<Toolbar>(R.id.behavior_toolbar)
        val rv = findViewById<RecyclerView>(R.id.behavior_rv)
        val vi = findViewById<View>(R.id.vi)

        toolbar.setTitle(R.string.behavior_test_demo_name)
        setSupportActionBar(toolbar)

        // 解决方案一: OnOffsetChangedListener监听AppBar的offset
        // 存储可滚动（内容）视图的初始高度
//        var rvOriginalHeight = 0
//        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
//            log.d(tag, "rv.height: ${rv.height} ==> rvOriginalHeight: $rvOriginalHeight - offset: $offset  ")
//
//            if (rvOriginalHeight <= 0) {
//                // CoordinatorLayout在layout时，会通过HeaderScrollingViewBehavior为所有子view的高度追加AppBar可滚动的
//                // 高度范围，因此，view第一次layout时，内容视图的高度实际会比较大，此时被顶出屏幕范围的情况已经发生了。因此需要
//                // 减去AppBar的高度，作为初始高度。
//                rvOriginalHeight = rv.height - appBarLayout.height
//            }
//
//            if (rvOriginalHeight > 0) {
//                // 根据AppBar的offset，动态改变内容视图的高度，并请求重绘。
//                rv.let {
//                    it.layoutParams.height = rvOriginalHeight - offset
//                    it.requestLayout()
//                }
//            }
//        })

        rv.apply {
            layoutManager = LinearLayoutManager(this@BehaviorTestActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = RandomAdapter(20)
        }

        vi.setOnClickListener {
            rv.smoothScrollToPosition(0)
            appBarLayout.setExpanded(true, true)
        }
    }

    companion object {
        val demoItem = DemoItem("behavior_test",
            R.string.behavior_test_demo_name,
            R.string.behavior_test_describe,
            BehaviorTestActivity::class.java
        )
    }
}

private class RandomAdapter(val size: Int) : RecyclerView.Adapter<RandomAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_random_list, parent, false)
        return VH(view).apply {
            textView = view.findViewById(R.id.random_list_tv)
        }
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.textView.text = "$position"
        val backgroundColor = Color.rgb(
            random.nextInt(255),
            random.nextInt(255),
            random.nextInt(255)
        )
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var textView: TextView
    }

    private val random: Random = Random(Date().time)
}