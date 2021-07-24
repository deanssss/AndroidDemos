package xyz.dean.androiddemos.demos.infinite_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 真-无限循环
 */
class InfiniteLayoutManager : RecyclerView.LayoutManager() {
    private var placedChildCount = 0

    override fun isAutoMeasureEnabled() = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 ) return
        if (state.isPreLayout) return

        detachAndScrapAttachedViews(recycler)

        initAdd(recycler)
    }

    private fun initAdd(recycler: RecyclerView.Recycler) {
        placedChildCount = 0
        val addStart = width / 2

        var leftStart = addStart
        var rightStart = addStart

        // add center child if exists.
        val centerPos = if((itemCount and 1) == 0) -1 else (itemCount - 1) / 2
        val centerView = if(centerPos == -1) null else recycler.getViewForPosition(centerPos)
        centerView?.let {
            addView(it)
            measureChildWithMargins(it, 0, 0)
            val width = getDecoratedMeasuredWidth(it)
            val height = getDecoratedMeasuredHeight(it)
            layoutDecorated(it, addStart - width / 2, 0, addStart + width, height)

            placedChildCount++

            leftStart -= width / 2
            rightStart += width / 2
        }

        var leftStartPos = (itemCount - 1) / 2
        var rightStartPos = (itemCount - 1) / 2 + 1
        if (centerPos != -1) {
            leftStartPos = prevChildPos(centerPos)
            rightStartPos = nextChildPos(centerPos)
        }

        // add left children
        addLeftChildren(leftStart, leftStartPos, recycler)
        // add right children
        addRightChildren(rightStart, rightStartPos, recycler)
    }

    private fun prevChildPos(currentChildPos: Int): Int {
        return if (currentChildPos - 1 < 0) itemCount - 1
        else currentChildPos - 1
    }

    private fun nextChildPos(currentChildPos: Int): Int {
        return if (currentChildPos + 1 >= itemCount) 0
        else currentChildPos + 1
    }

    private fun addLeftChildren(startX: Int, startPos: Int, recycler: RecyclerView.Recycler) {
        var leftStart = startX
        var leftStartPos = startPos
        while (leftStart > -width && placedChildCount <= 100) {
            val child = recycler.getViewForPosition(leftStartPos)
            addView(child, 0)
            measureChildWithMargins(child, 0, 0)
            val width = getDecoratedMeasuredWidth(child)
            val height = getDecoratedMeasuredHeight(child)
            layoutDecorated(child, leftStart - width, 0, leftStart, height)

            placedChildCount++
            leftStart -= width
            leftStartPos = prevChildPos(leftStartPos)
        }
    }

    private fun addRightChildren(startX: Int, startPos: Int, recycler: RecyclerView.Recycler) {
        var rightStart = startX
        var rightStartPos = startPos
        while (rightStart < width * 2 && placedChildCount <= 100) {
            val child = recycler.getViewForPosition(rightStartPos)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val width = getDecoratedMeasuredWidth(child)
            val height = getDecoratedMeasuredHeight(child)
            layoutDecorated(child, rightStart, 0, rightStart + width, height)

            placedChildCount++
            rightStart += width
            rightStartPos = nextChildPos(rightStartPos)
        }
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        fill(dx, recycler)
        offsetChildrenHorizontal(-1 * dx)

        return dx
    }

    private fun fill(dx: Int, recycler: RecyclerView.Recycler) {
        recycleOut(dx, recycler)

        if (dx > 0) {
            // scroll to left, add child behind the last child.
            val lastView = getChildAt(childCount - 1)
            if (lastView != null) {
                val lastPos = getPosition(lastView)
                if (lastView.right - dx < width * 2) {
                    val rightStart = lastView.right
                    val rightStartPos = nextChildPos(lastPos)
                    addRightChildren(rightStart, rightStartPos, recycler)
                }
            } else {
                initAdd(recycler)
            }
        } else {
            // scroll to right, add child before the first child.
            val firstView = getChildAt(0)
            if (firstView != null) {
                val firstPos = getPosition(firstView)
                if (firstView.left - dx  > -width ) {
                    val leftStart = firstView.left
                    val leftStartPos = prevChildPos(firstPos)
                    addLeftChildren(leftStart, leftStartPos, recycler)
                }
            } else {
                initAdd(recycler)
            }
        }
    }

    private fun recycleOut(dx: Int, recycler: RecyclerView.Recycler) {
        if (dx > 0) {
            // scroll to left, remove the view in left.
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                view?.let {
                    if (it.left - dx < -width) {
                        removeAndRecycleView(view, recycler)
                        placedChildCount--
                    }
                }
            }
        } else {
            // scroll to right, remove the view in right.
            for (i in childCount-1 downTo 0) {
                val view = getChildAt(i)
                view?.let {
                    if (it.right - dx > width * 2) {
                        removeAndRecycleView(view, recycler)
                        placedChildCount--
                    }
                }
            }
        }
    }
}
