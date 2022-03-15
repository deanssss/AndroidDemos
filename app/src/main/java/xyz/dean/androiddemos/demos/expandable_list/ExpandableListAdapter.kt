package xyz.dean.androiddemos.demos.expandable_list

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class ExpandableListAdapter<D>
    : RecyclerView.Adapter<ExpandableListAdapter.ExpandableVH<D, out ViewDataBinding>>()
{
    /** 只允许展开一个Item的模式 */
    open val expandOneMode: Boolean = false
    private var _expandItem = -1
    val expandItem: Int get() = _expandItem
    private val _expandItems = mutableListOf<Int>()
    val expandItems: List<Int> get() = _expandItems

    private var recyclerView: RecyclerView? = null

    abstract fun getItemData(position: Int): D

    override fun onBindViewHolder(holder: ExpandableVH<D, out ViewDataBinding>, position: Int) {
        val isExpand = if (expandOneMode) _expandItem == position else _expandItems.contains(position)
        holder.bindData(position, isExpand, getItemData(position), this::onExpandStateChanged)
    }

    private fun onExpandStateChanged(position: Int) {
        val scrollTo = if (!expandOneMode) {
            if (_expandItems.contains(position)) {
                _expandItems.remove(position)
                notifyItemChanged(position)
                -1
            } else {
                _expandItems.add(position)
                notifyItemChanged(position)
                position
            }
        } else {
            val oldExpanded = _expandItem
            _expandItem = when (_expandItem) {
                -1 -> position      // 之前没有展开的item，展开此item
                position -> -1      // 当前item之前是展开的，折叠此item
                else -> position    // 之前展开的item与当前要展开的item不一样，折叠之前的，展开当前的
            }
            if (oldExpanded != -1) notifyItemChanged(oldExpanded)
            if (_expandItem != -1) notifyItemChanged(_expandItem)
            _expandItem
        }

        if (scrollTo != -1) {
            // 如果要展开的item没有显示完全，则滚动列表使其显示完全。
            (recyclerView?.layoutManager as? LinearLayoutManager)?.let {
                val first = it.findFirstCompletelyVisibleItemPosition()
                val last = it.findLastCompletelyVisibleItemPosition()
                if (scrollTo > last || scrollTo < first) {
                    it.isSmoothScrollbarEnabled = true
                    recyclerView?.smoothScrollToPosition(scrollTo)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    fun clearExpandState() {
        _expandItems.clear()
        _expandItem = -1
    }

    abstract class ExpandableVH<D, T : ViewDataBinding>(
        val binding: T
    ) : RecyclerView.ViewHolder(binding.root) {
        abstract val mainItemLayout: ViewGroup
        abstract val expandLayout: ViewGroup

        internal fun bindData(
            position: Int, isExpand: Boolean, data: D,
            onExpandChanged: (position: Int) -> Unit,
        ) {
            bindData(position, data)
            expandLayout.isVisible = isExpand
            mainItemLayout.setOnClickListener { onExpandChanged(position) }
        }

        abstract fun bindData(position: Int, data: D)
    }
}
