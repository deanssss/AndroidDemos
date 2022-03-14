package xyz.dean.androiddemos.demos.expandable_list

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.databinding.ItemExpandableListBinding

class ExpandableListAdapter : RecyclerView.Adapter<ExpandableListAdapter.ExpandableVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemExpandableListBinding.inflate(layoutInflater, parent, false)
        return ExpandableVH(binding)
    }

    override fun onBindViewHolder(holder: ExpandableVH, position: Int) {
        holder.binding.tvName.text = "$position"
        holder.binding.isExpand = false
        holder.binding.itemMainLayout.setOnClickListener {
            holder.binding.apply {
                if (isExpand != true) {
                    isExpand = true
                    itemExpandLayout.animate().scaleY(0f).alpha(0f).start()
                } else {
                    isExpand = false
                    itemExpandLayout.animate().scaleY(1f).alpha(1f).start()
                }
            }
//            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = 10

    class ExpandableVH(
        val binding: ItemExpandableListBinding
    ) : RecyclerView.ViewHolder(binding.root)
}