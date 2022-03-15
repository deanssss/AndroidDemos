package xyz.dean.androiddemos.demos.expandable_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.databinding.ActivityExpandableListBinding
import xyz.dean.androiddemos.databinding.ItemExpandableListBinding

class ExpandableListActivity : BaseActivity() {
    private lateinit var binding: ActivityExpandableListBinding

    override fun getDemoItem() = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expandable_list)
        binding.lifecycleOwner = this
        binding.expandableList.adapter = DExpandableListAdapter()
    }

    class DExpandableListAdapter : ExpandableListAdapter<Int>() {
        override val expandOneMode: Boolean = true
        override fun getItemData(position: Int) = position

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ExpandableVH<Int, out ViewDataBinding> {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemExpandableListBinding.inflate(layoutInflater, parent, false)
            return VH(binding)
        }

        override fun getItemCount(): Int = 30

        class VH(
            binding: ItemExpandableListBinding
        ) : ExpandableVH<Int, ItemExpandableListBinding>(binding) {
            override val mainItemLayout = binding.itemMainLayout
            override val expandLayout = binding.itemExpandLayout

            override fun bindData(position: Int, data: Int) {
                binding.tvName.text = "$position"
            }
        }
    }

    companion object {
        val demoItem = DemoItem("expandable-list",
            R.string.expandable_list_demo_name,
            R.string.expandable_list_describe,
            ExpandableListActivity::class.java, R.mipmap.img_practice)
    }
}