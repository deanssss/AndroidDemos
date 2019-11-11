package xyz.dean.androiddemos.demos.infinite_list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.common.log

/**
 * 真-无限循环
 */
class InfiniteLayoutManager : RecyclerView.LayoutManager() {
    private val tag: String = this.javaClass.simpleName
    private var placedChildCount = 0
    private var firstChild: View? =null
    private var lastChild: View? = null

    override fun isAutoMeasureEnabled() = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 ) return
        if (state.isPreLayout) return
        log.d(tag, "on layout")

        detachAndScrapAttachedViews(recycler)

        recycler.setViewCacheSize(30)
        initAdd(recycler, state)
    }

    private fun initAdd(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        placedChildCount = 0
        val addStart = width / 2

        var leftStart = addStart
        var rightStart = addStart

        // add center child
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
        while (leftStart > -width && placedChildCount <= 30) {
            val child = recycler.getViewForPosition(leftStartPos)
            addView(child, 0)
            measureChildWithMargins(child, 0, 0)
            val width = getDecoratedMeasuredWidth(child)
            val height = getDecoratedMeasuredHeight(child)
            layoutDecorated(child, leftStart - width, 0, leftStart, height)

            placedChildCount++
            leftStart -= width
            leftStartPos = prevChildPos(leftStartPos)
            firstChild = child
        }

        // add right children
        while (rightStart < width * 2 && placedChildCount <= 30) {
            val child = recycler.getViewForPosition(rightStartPos)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val width = getDecoratedMeasuredWidth(child)
            val height = getDecoratedMeasuredHeight(child)
            layoutDecorated(child, rightStart, 0, rightStart + width, height)

            placedChildCount++
            rightStart += width
            rightStartPos = nextChildPos(rightStartPos)
            lastChild = child
        }
    }

    private fun prevChildPos(currentChildPos: Int): Int {
        return if (currentChildPos - 1 < 0) itemCount - 1
        else currentChildPos - 1
    }

    private fun nextChildPos(currentChildPos: Int): Int {
        return if (currentChildPos + 1 >= itemCount) 0
        else currentChildPos + 1
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        fill(dx, recycler, state)
        offsetChildrenHorizontal(-1 * dx)

        return dx
    }

    private fun fill(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        log.d(tag, "dx: $dx scroll to left: ${dx > 0}")
        val fiView = getChildAt(0)!!
        val fiPos = getPosition(fiView)
        log.d(tag, "current first pos: $fiPos current first left: ${fiView.left}")
        val laView = getChildAt(itemCount - 1)!!
        val laPos = getPosition(laView)
        log.d(tag, "current last pos: $laPos current last right: ${laView.right}")
        recycleOut(dx, recycler, state)

        if (dx > 0) {
            // scroll to left, add child behind the last child.
            val lastView = getChildAt(childCount - 1)
            if (lastView != null) {
                val lastPos = getPosition(lastView)
                if (lastView.right - dx < width * 2) {
                    var rightStart = lastView.right
                    var rightStartPos = nextChildPos(lastPos)

                    while (rightStart < width * 2 && placedChildCount <= 30) {
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
            } else {
                initAdd(recycler, state)
            }
        } else {
            // scroll to right, add child before the first child.
            val firstView = getChildAt(0)
            if (firstView != null) {
                val firstPos = getPosition(firstView)
                if (firstView.left - dx  > -width ) {
                    var leftStart = firstView.left
                    var leftStartPos = prevChildPos(firstPos)

                    while (leftStart > -width && placedChildCount <= 30) {
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
            } else {
                initAdd(recycler, state)
            }
        }
    }

    private fun recycleOut(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
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
