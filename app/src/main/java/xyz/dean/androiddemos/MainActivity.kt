package xyz.dean.androiddemos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.demos.infinite_list.InfiniteTabActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val demoList = listOf(
            DemoItem("无限循环列表", "一个真正的无限循环列表，通过RecyclerView的LayoutManager实现。", InfiniteTabActivity::class.java)
        )

        findViewById<RecyclerView>(R.id.rv_practice_list).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = PracticeListAdapter(demoList)
        }
    }
}

private class PracticeListAdapter(private val practiceList: List<DemoItem>) : RecyclerView.Adapter<PracticeListAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): VH {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.item_practice, parent, false)
        return VH(rootView)
    }

    override fun getItemCount(): Int = practiceList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.findViewById<ImageView>(R.id.iv_practice_icon).setImageResource(practiceList[position].iconRes)
        holder.itemView.findViewById<TextView>(R.id.tv_practice_name).text = practiceList[position].name
        holder.itemView.findViewById<TextView>(R.id.tv_practice_describe).text = practiceList[position].describe
        holder.itemView.setOnClickListener {
            holder.itemView.context.apply {
                startActivity(Intent(this, practiceList[position].clazz))
            }
        }
    }

    private class VH(view: View) : RecyclerView.ViewHolder(view)
}

private data class DemoItem(
    val name: String,
    val describe: String,
    val clazz: Class<out BaseActivity>,
    @DrawableRes val iconRes: Int = R.mipmap.img_practice)
