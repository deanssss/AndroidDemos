package xyz.dean.androiddemos.demos.infinite_list

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R

class InfiniteTabActivity : BaseActivity() {
    override fun getDemoItem() = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infinite_list)

        val data = listOf("盖浇饭", "叉烧包", "炒饭", "特惠套餐", "夏日凉饮", "麻辣烫", "特色小菜", "每日推荐", "煲仔饭", "黄焖鸡")
        val tab = findViewById<ScrollCenterRecyclerView>(R.id.infinite_rv)
        tab.apply {
            adapter = RAdapter(data, this)
            layoutManager = InfiniteLayoutManager()
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        findViewById<Button>(R.id.bt_confirm).setOnClickListener {
            val str = findViewById<EditText>(R.id.et_position).text.toString()
            val position = if (TextUtils.isEmpty(str)) 0 else str.toInt()
            tab.scrollToCenter(position)
        }
    }

    companion object {
        val demoItem = DemoItem("infinite",
            R.string.infinite_demo_name,
            R.string.infinite_demo_describe,
            InfiniteTabActivity::class.java)
    }
}

private class RAdapter(val datas: List<String>,val recyclerView: ScrollCenterRecyclerView) : RecyclerView.Adapter<RAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_infinite_rv, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val name = "${datas[position]} $position"

        holder.itemView.findViewById<TextView>(R.id.tv_name).text = name
        holder.itemView.setOnClickListener {
            recyclerView.scrollToCenter(position)
        }
    }

    private class VH(view: View) : RecyclerView.ViewHolder(view)
}
