package xyz.dean.androiddemos.demos.stackcard

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import java.util.Date
import kotlin.random.Random

class StackCardActivity : BaseActivity() {

    private val stackCardAdapter = StackCardAdapter()

    override fun getDemoItem(): DemoItem = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_card)
        val recyclerView = findViewById<RecyclerView>(R.id.stack_card_list)
        recyclerView.apply {
            this.adapter = stackCardAdapter
            layoutManager = StackCardLayoutManager()
            ItemTouchHelper(object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder
                ): Int {
                    return makeMovementFlags(
                        ItemTouchHelper.START or ItemTouchHelper.END,
                        ItemTouchHelper.START or ItemTouchHelper.END
                    )
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    target: ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    log.d(this@StackCardActivity.tag, "remove position: $position")
                    stackCardAdapter.data.removeAt(position)
                    stackCardAdapter.notifyItemRemoved(position)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    viewHolder.itemView.translationX = dX
                    viewHolder.itemView.translationY = dY
                }
            }).attachToRecyclerView(this)
        }
    }

    companion object {
        val demoItem = DemoItem("stack_card",
            R.string.stack_card_demo_name,
            R.string.stack_card_describe_text,
            StackCardActivity::class.java, R.mipmap.img_practice)
    }
}

class StackCardAdapter : RecyclerView.Adapter<StackCardAdapter.VH>() {

    val data = (0 until 10).toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stack_card_layout, parent, false)
            .let { VH(it) }
    }

    override fun getItemCount(): Int = data.size

    private val random: Random = Random(Date().time)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
//        holder.itemView.setBackgroundColor(
//            Color.rgb(
//                random.nextInt(255),
//                random.nextInt(255),
//                random.nextInt(255)
//            )
//        )
        holder.itemView.elevation = ((position + 1) * 10).toFloat()
        holder.itemView.findViewById<TextView>(R.id.stack_card_item_tv)
            .text = "card ${data[position]}"
    }

    class VH(root: View) : ViewHolder(root)
}
