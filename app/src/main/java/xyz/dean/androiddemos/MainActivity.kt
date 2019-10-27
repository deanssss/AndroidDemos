package xyz.dean.androiddemos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.rv_practice_list).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = PracticeListAdapter(demos.values.toList())
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}

private class PracticeListAdapter(private val practiceList: List<DemoItem>) : RecyclerView.Adapter<PracticeListAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): VH {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.item_practice, parent, false)
        return VH(rootView)
    }

    override fun getItemCount(): Int = practiceList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val context = holder.itemView.context
        holder.itemView.findViewById<ImageView>(R.id.iv_practice_icon).setImageResource(practiceList[position].iconRes)
        holder.itemView.findViewById<TextView>(R.id.tv_practice_name).text = context.getString(practiceList[position].nameRes)
        holder.itemView.findViewById<TextView>(R.id.tv_practice_describe).text = context.getString(practiceList[position].describeRes)
        holder.itemView.setOnClickListener {
            holder.itemView.context.apply {
                startActivity(Intent(this, practiceList[position].clazz))
            }
        }
    }

    private class VH(view: View) : RecyclerView.ViewHolder(view)
}
