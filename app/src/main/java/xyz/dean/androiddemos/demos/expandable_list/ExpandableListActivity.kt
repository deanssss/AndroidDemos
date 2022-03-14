package xyz.dean.androiddemos.demos.expandable_list

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.databinding.ActivityExpandableListBinding

class ExpandableListActivity : BaseActivity() {
    private lateinit var binding: ActivityExpandableListBinding

    override fun getDemoItem() = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expandable_list)
        binding.lifecycleOwner = this
        binding.expandableList.adapter = ExpandableListAdapter()
    }

    companion object {
        val demoItem = DemoItem("expandable-list",
            R.string.expandable_list_demo_name,
            R.string.expandable_list_demo_name,
            ExpandableListActivity::class.java, R.mipmap.img_practice)
    }
}