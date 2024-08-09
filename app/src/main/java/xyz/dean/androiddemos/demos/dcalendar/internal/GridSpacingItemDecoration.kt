package xyz.dean.androiddemos.demos.dcalendar.internal

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State

class GridSpaceItemDecoration @JvmOverloads constructor(
    private val mSpacing: Int,
    private val mIncludeEdge: Boolean = true
) : ItemDecoration() {
    private val halfSpacing = (mSpacing / 2f + 0.5).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            if (mIncludeEdge) {
                outRect.set(halfSpacing, halfSpacing, halfSpacing, halfSpacing)
            } else {
                val spanCount = layoutManager.spanCount
                val spanLookup = layoutManager.spanSizeLookup

                val spanSize = spanLookup.getSpanSize(position)
                val spanIndex = spanLookup.getSpanIndex(position, spanCount)
                val lastItemInTopRow = getLastItemInTopRow(spanLookup, spanCount, state)
                val firstItemInBottomRow = getFirstItemInBottomRow(spanLookup, spanCount, state)

                // 上边距
                outRect.top = if (position <= lastItemInTopRow) 0 else halfSpacing
                // 左边距
                outRect.left = if (spanIndex == 0) 0 else halfSpacing
                // 下边距
                outRect.bottom = if (position >= firstItemInBottomRow) 0 else halfSpacing
                // 右边距
                outRect.right = if (spanIndex + spanSize == spanCount) 0 else halfSpacing
            }
        }
    }

    private fun getLastItemInTopRow(spanLookup: SpanSizeLookup, spanCount: Int, state: State): Int {
        val lastPos = state.itemCount - 1
        if (lastPos == 0) return 0

        for (i in 1 ..  lastPos) {
            if (spanLookup.getSpanIndex(i, spanCount) == 0) return i - 1
        }
        return 0
    }

    private fun getFirstItemInBottomRow(spanLookup: SpanSizeLookup, spanCount: Int, state: State): Int {
        val lastPos = state.itemCount - 1
        for (i in lastPos downTo 0) {
            if (spanLookup.getSpanIndex(i, spanCount) == 0) return i
        }
        return 0
    }
}