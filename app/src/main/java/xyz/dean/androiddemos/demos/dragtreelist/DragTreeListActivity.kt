package xyz.dean.androiddemos.demos.dragtreelist

import android.app.Service
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import xyz.dean.androiddemos.databinding.ActivityDragTreeListBinding
import xyz.dean.androiddemos.databinding.ItemDragListBinding
import xyz.dean.androiddemos.databinding.ItemDragListChildBinding

class DragTreeListActivity : BaseActivity() {
    private lateinit var binding: ActivityDragTreeListBinding
    private val groupList = mutableListOf(
        Triple(1, "GroupA", true),
        Triple(1, "A", false),
        Triple(1, "AA", false),
        Triple(1, "AAA", false),
        Triple(2, "GroupB", true),
        Triple(2, "B", false),
        Triple(2, "BB", false),
        Triple(2, "ABB", false),
        Triple(3, "GroupC", true),
        Triple(3, "C", false),
        Triple(3, "CC", false),
        Triple(3, "CCC", false),
        Triple(4, "GroupD", true),
        Triple(4, "D", false),
        Triple(4, "DD", false),
        Triple(4, "DDD", false),
        Triple(5, "GroupE", true),
        Triple(5, "E", false),
        Triple(5, "EE", false),
        Triple(5, "EEE", false),
    )
    override fun getDemoItem() = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_drag_tree_list)

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            private var dragGroup: List<Triple<Int, String, Boolean>>?= null

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val flags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(flags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val dragPosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                log.d("DDDD", "Drag from: $dragPosition --> $targetPosition")
                val adapter = recyclerView.adapter ?: return false
                if (groupList[dragPosition].third) {
                    // 移动组
                    val targetItem = groupList[targetPosition]
                    val targetGroup = groupList.asSequence()
                        .mapIndexed { index, item -> index to item }
                        .filter { it.second.first == targetItem.first }
                        .toList()
                    val moveInterval = if (dragPosition < targetPosition) {
                        // 往下拖拽，如果拖拽区间中有一整个分组，才move，且只move包含的这个分组。
                        val targetGroupLastPos = targetGroup.last().first
                        val intervalEnd = if (targetPosition < targetGroupLastPos) {
                            // 没有包含当前分组，检测上一个分组是否被包含（不是被拖拽的分组就代表此分组被包含了）
                            val prevGroupLastPos = targetGroup[0].first - 1
                            if (dragPosition >= prevGroupLastPos) {
                                // 上一个分组就是被拖拽的分组
                                return false
                            }
                            prevGroupLastPos
                        } else {
                            // 包含当前分组
                            targetGroupLastPos
                        }
                        dragPosition .. intervalEnd
                    } else {
                        // 往上拖拽，如果拖拽区间中有一整个分组，才move，且只move包含的这个分组。
                        val intervalEnd = if (!targetItem.third) {
                            val nextGroupFirstPos = targetGroup.last().first + 1
                            if (dragPosition <= nextGroupFirstPos) {
                                // 下一分组就是被拖拽的分组
                                return false
                            }
                            nextGroupFirstPos
                        } else {
                            targetPosition
                        }
                        dragPosition downTo intervalEnd
                    }
                    log.d("DDDD", "move interval: $dragPosition --> ${moveInterval.last}")
                    moveInterval.reduce { prev, current ->
                        moveChildItem(adapter, prev, current)
                        current
                    }
                } else {
                    if (targetPosition <= 0) // 拖动Item不能越过第一个item
                        return false

                    val moveInterval = if (dragPosition < targetPosition) {
                        // 拖动Item向下移动，区间中每个item都往上移动一格
                        dragPosition .. targetPosition
                    } else {
                        // 拖动Item向上移动，区间中每个item都往下移动一格
                        dragPosition downTo targetPosition
                    }
                    moveInterval.reduce { prev, current ->
                        moveChildItem(adapter, prev, current)
                        current
                    }
                }
                return true
            }

            private fun moveChildItem(adapter: RecyclerView.Adapter<*>, fromPosition: Int, targetPosition: Int) {
                log.d("DDDD", "[Move Child] from: $fromPosition target: $targetPosition")
                val fromItem = groupList[fromPosition]
                val targetItem = groupList[targetPosition]

                val addedItem = if (
                    fromItem.third // 分组item移动
                    || (targetItem.first == fromItem.first && !targetItem.third) // 组内移动，没越过分组item
                ) {
                    fromItem
                } else if (targetItem.third) {
                    // 越过了分组item
                    if (targetPosition == 0) return // 第一组的分组item不可越过
                    val groupId = if (fromPosition < targetPosition) {
                        groupList[targetPosition].first
                    } else {
                        groupList[targetPosition - 1].first
                    }
                    fromItem.copy(first = groupId)
                } else {
                    // 移动到了其它分组内部
                    fromItem.copy(first = targetItem.first)
                }
                groupList[fromPosition] = targetItem
                groupList[targetPosition] = addedItem

                adapter.notifyItemMoved(fromPosition, targetPosition)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun isLongPressDragEnabled(): Boolean = true

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder?.itemView?.setBackgroundColor(Color.RED)
                    (getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(70)
                    val dragPosition = viewHolder?.adapterPosition ?: return

                    if (groupList[dragPosition].third) {
                        val groupId = groupList[dragPosition].first
                        dragGroup = groupList.filter { it.first == groupId }
                        groupList.removeAll(dragGroup!!.takeLast(dragGroup!!.size - 1))
                        binding.listRv.adapter?.notifyItemRangeRemoved(dragPosition + 1, dragGroup!!.size - 1)
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundColor(0)
                val dragPosition = viewHolder.adapterPosition
                if (dragPosition >= 0 && groupList[dragPosition].third) {
                    groupList.addAll(dragPosition + 1, dragGroup?.takeLast(dragGroup!!.size - 1) ?: emptyList())
                    recyclerView.adapter?.notifyItemRangeInserted(dragPosition + 1, dragGroup!!.size - 1)
                }
            }
        })
        binding.listRv.apply {
            adapter = GroupListAdapter(groupList)
            touchHelper.attachToRecyclerView(this)
        }
    }

    class GroupListAdapter(
        private val data: MutableList<Triple<Int,String, Boolean>>,
    ) : RecyclerView.Adapter<DragListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DragListViewHolder {
            return if (viewType == 1) {
                VHGroup(ItemDragListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            } else {
                VHChild(ItemDragListChildBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

        override fun onBindViewHolder(holder: DragListViewHolder, position: Int) {
            if (getItemViewType(position) == 1) {
                holder as VHGroup
                holder.binding.groupNameTv.text = data[position].second
            } else {
                holder as VHChild
                holder.binding.childNameTv.text = data[position].second
            }
        }

        override fun getItemCount() = data.size

        override fun getItemViewType(position: Int) =
            if (data[position].third) 1 else 0
    }

    abstract class DragListViewHolder(open val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
    class VHGroup(override val binding: ItemDragListBinding) : DragListViewHolder(binding)
    class VHChild(override val binding: ItemDragListChildBinding) : DragListViewHolder(binding)

    companion object {
        val demoItem = DemoItem("drag-list",
            R.string.drag_list_demo_name,
            R.string.drag_list_describe,
            DragTreeListActivity::class.java, R.mipmap.img_practice)
    }
}