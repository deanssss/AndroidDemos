package xyz.dean.androiddemos.demos.infinite_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 真-无限循环
 */
class InfiniteLayoutManager: RecyclerView.LayoutManager() {
    override fun isAutoMeasureEnabled() = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0 ) {
            detachAndScrapAttachedViews(recycler)
            return
        }
        detachAndScrapAttachedViews(recycler)
        var actualWidth = 0
        for (i in 0 .. itemCount) {
            val scrap= recycler.getViewForPosition(i)
            addView(scrap)
            measureChildWithMargins(scrap, 0, 0)
            val width = getDecoratedMeasuredWidth(scrap)
            val height = getDecoratedMeasuredHeight(scrap)
            layoutDecorated(scrap, actualWidth, 0, actualWidth + width, height)
            actualWidth += width

            if (actualWidth > getWidth()) break
        }
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        fill(dx, recycler, state)
        offsetChildrenHorizontal(-1 * dx)

        return dx
    }

    private fun fill(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        recycleOut(dx, recycler, state)

        if (dx > 0) {
            val lastView = getChildAt(childCount - 1)
            if (lastView != null) {
                var lastPos = getPosition(lastView)
                if (lastView.right - dx < width) {
                    val childWidth = getDecoratedMeasuredWidth(lastView)
                    val addCount = dx / childWidth
                    var widthOffset = lastView.right
                    for (i in 0 .. addCount) {
                        val scrap = recycler.getViewForPosition(
                            if (lastPos == itemCount - 1) {
                                lastPos = 0
                                lastPos
                            } else {
                                ++lastPos
                            }
                        )
                        addView(scrap)

                        measureChildWithMargins(scrap, 0, 0)
                        val width = getDecoratedMeasuredWidth(scrap)
                        layoutDecorated(scrap, widthOffset, 0, widthOffset + width, lastView.bottom)
                        widthOffset += width
                    }
                }
            } else {
                var actualWidth = 0
                for (i in 0 .. itemCount) {
                    val scrap= recycler.getViewForPosition(i)
                    addView(scrap)
                    measureChildWithMargins(scrap, 0, 0)
                    val width = getDecoratedMeasuredWidth(scrap)
                    val height = getDecoratedMeasuredHeight(scrap)
                    layoutDecorated(scrap, actualWidth, 0, actualWidth + width, height)
                    actualWidth += width

                    if (actualWidth > getWidth()) break
                }
            }
        } else {
            val firstView = getChildAt(0)
            if (firstView != null) {
                var firstPos = getPosition(firstView)
                if (firstView.left >= 0 ) {
                    val childWidth = getDecoratedMeasuredWidth(firstView)
                    val addCount = dx / childWidth
                    var widthOffset = firstView.left

                    for (i in 0 .. addCount) {
                        val scrap = recycler.getViewForPosition(
                            if (firstPos == 0 ) {
                                firstPos = itemCount - 1
                                firstPos
                            } else {
                                --firstPos
                            }
                        )
                        addView(scrap, 0)

                        measureChildWithMargins(scrap, 0, 0)
                        val width = getDecoratedMeasuredWidth(scrap)
                        getDecoratedMeasuredHeight(scrap)
                        layoutDecorated(scrap, widthOffset - width, 0, widthOffset, firstView.bottom)
                        widthOffset -= width
                    }
                }
            } else {
                var actualWidth = 0
                for (i in 0 .. itemCount) {
                    val scrap= recycler.getViewForPosition(i)
                    addView(scrap)
                    measureChildWithMargins(scrap, 0, 0)
                    val width = getDecoratedMeasuredWidth(scrap)
                    val height = getDecoratedMeasuredHeight(scrap)
                    layoutDecorated(scrap, actualWidth, 0, actualWidth + width, height)
                    actualWidth += width

                    if (actualWidth > getWidth()) break
                }
            }
        }
    }

    private fun recycleOut(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        for (i in childCount-1 downTo 0) {
            val view = getChildAt(i)!!

            if (dx > 0) {
                if (view.left - dx < - width ) {
                    removeAndRecycleView(view, recycler)
                }
            } else {
                if (view.right - dx > width * 2) {
                    removeAndRecycleView(view, recycler)
                }
            }
        }
    }
}
