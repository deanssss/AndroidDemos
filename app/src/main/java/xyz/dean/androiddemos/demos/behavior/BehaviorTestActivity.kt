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
import xyz.dean.androiddemos.common.log
import java.util.*
import kotlin.random.Random

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

        toolbar.setTitle(R.string.behavior_test_demo_name)
        setSupportActionBar(toolbar)

        var rvHeight = 0
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
            log.d(tag, "offset: $offset rvHeight: $rvHeight rv.height: ${rv.height}")

            if (rvHeight <= 0) {
                rvHeight = rv.height - appBarLayout.height
            }

            if (rvHeight > 0) {
                rv.let {
                    it.layoutParams.height = rvHeight - offset
                    it.requestLayout()
                }
            }
        })

        rv.apply {
            layoutManager = LinearLayoutManager(this@BehaviorTestActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = RandomAdapter(20)
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

    private class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var textView: TextView
    }

    private val random: Random = Random(Date().time)
}