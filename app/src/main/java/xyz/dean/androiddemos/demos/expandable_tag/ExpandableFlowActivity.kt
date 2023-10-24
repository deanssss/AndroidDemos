package xyz.dean.androiddemos.demos.expandable_tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R

class ExpandableFlowActivity : BaseActivity() {

    override fun getDemoItem(): DemoItem = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expandable_flow)
        val flowLayout = findViewById<ExpandableFlowLayout>(R.id.flowLayout)

        val data = listOf("盖浇饭盖浇饭盖浇饭盖浇饭盖浇饭盖浇饭盖浇饭盖浇饭", "叉烧包", "炒饭", "特惠套餐",
            "夏日凉饮", "麻辣烫", "特色小菜", "每日推荐", "煲仔饭", "黄焖鸡", "每日推荐", "煲仔饭",
            "黄焖鸡", "每日推荐", "煲仔饭", "黄焖鸡")
        flowLayout.setAdapter(object : ExpandableFlowLayout.Adapter<String>(data) {
            override fun getView(parent: ExpandableFlowLayout, position: Int): View {
                val item = getItem(position)
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_infinite_rv, flowLayout, false)
                view.findViewById<TextView>(R.id.tv_name).text = item
                return view
            }
        })
        flowLayout.onItemClickListener = {
            Toast.makeText(this, "click $it", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val demoItem = DemoItem("sample",
            R.string.expandable_flow_demo_name,
            R.string.expandable_flow_describe_text,
            ExpandableFlowActivity::class.java, R.mipmap.img_practice)
    }
}
