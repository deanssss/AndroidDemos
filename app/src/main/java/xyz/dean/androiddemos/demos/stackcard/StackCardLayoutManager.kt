package xyz.dean.androiddemos.demos.stackcard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.floor
import kotlin.math.pow


class StackCardLayoutManager : RecyclerView.LayoutManager() {
    private val TAG = "EchelonLayoutManager"

    private var mItemViewWidth = 0
    private var mItemViewHeight = 0
    private var mItemCount = 0
    private var mScrollOffset = Int.MAX_VALUE

    init {
        mItemViewWidth = (getHorizontalSpace() * 0.87f).toInt()
        mItemViewHeight = (mItemViewWidth * 1.46f).toInt()
    }

    override fun canScrollVertically(): Boolean = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0 || state.isPreLayout) return
        removeAndRecycleAllViews(recycler)
        mItemViewWidth = (getHorizontalSpace() * 0.87f).toInt()
        mItemViewHeight = (mItemViewWidth * 1.46f).toInt()
        mItemCount = itemCount
        mScrollOffset = mItemViewHeight.coerceAtLeast(mScrollOffset)
            .coerceAtMost(mItemCount * mItemViewHeight)
        layoutChild(recycler)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        val pendingScrollOffset = mScrollOffset + dy
        mScrollOffset = mItemViewHeight.coerceAtLeast(mScrollOffset + dy)
                .coerceAtMost(mItemCount * mItemViewHeight)
        layoutChild(recycler)
        return mScrollOffset - pendingScrollOffset + dy
    }

    private fun layoutChild(recycler: RecyclerView.Recycler) {
        if (itemCount == 0) return
        var bottomItemPosition = floor((mScrollOffset / mItemViewHeight).toDouble()).toInt()
        var remainSpace = getVerticalSpace() - mItemViewHeight
        val bottomItemVisibleHeight = mScrollOffset % mItemViewHeight
        val offsetPercentRelativeToItemView = bottomItemVisibleHeight * 1.0f / mItemViewHeight
        val layoutInfos: ArrayList<ItemViewInfo> = ArrayList()

        var i = bottomItemPosition - 1
        var j = 1
        while (i >= 0) {
            val maxOffset = (getVerticalSpace() - mItemViewHeight) / 2 * 0.8.pow(j.toDouble())
            val start = (remainSpace - offsetPercentRelativeToItemView * maxOffset).toInt()
            val layoutPercent = start * 1.0f / getVerticalSpace()
            val info = ItemViewInfo(start, offsetPercentRelativeToItemView, layoutPercent)
            layoutInfos.add(0, info)
            remainSpace = (remainSpace - maxOffset).toInt()
            if (remainSpace <= 0) {
                info.top = (remainSpace + maxOffset).toInt()
                info.positionOffset = 0f
                info.layoutPercent = info.top.toFloat() / getVerticalSpace()
                break
            }
            i--
            j++
        }

        if (bottomItemPosition < mItemCount) {
            val start = getVerticalSpace() - bottomItemVisibleHeight
            layoutInfos.add(
                ItemViewInfo(start, bottomItemVisibleHeight * 1.0f / mItemViewHeight, start * 1.0f / getVerticalSpace()).setIsBottom()
            )
        } else {
            bottomItemPosition -= 1 //99
        }
        val layoutCount = layoutInfos.size
        val startPos = bottomItemPosition - (layoutCount - 1)
        val endPos = bottomItemPosition
        val childCount = childCount
        for (i in childCount - 1 downTo 0) {
            val childView = getChildAt(i)
            val pos = getPosition(childView!!)
            if (pos > endPos || pos < startPos) {
                removeAndRecycleView(childView, recycler)
            }
        }
        detachAndScrapAttachedViews(recycler)
        for (i in 0 until layoutCount) {
            val view = recycler.getViewForPosition(startPos + i)
            val layoutInfo: ItemViewInfo = layoutInfos[i]
            addView(view)
            measureChildWithExactlySize(view)
            val left = (getHorizontalSpace() - mItemViewWidth) / 2
            layoutDecoratedWithMargins(
                view,
                left,
                layoutInfo.top,
                left + mItemViewWidth,
                layoutInfo.top + mItemViewHeight
            )
            view.pivotX = (view.width / 2).toFloat()
            view.pivotY = 0f
        }
    }

    /**
     * 测量itemview的确切大小
     */
    private fun measureChildWithExactlySize(child: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(mItemViewWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(mItemViewHeight, View.MeasureSpec.EXACTLY)
        child.measure(widthSpec, heightSpec)
    }

    /**
     * 获取RecyclerView的显示高度
     */
    private fun getVerticalSpace(): Int {
        return height - paddingTop - paddingBottom
    }

    /**
     * 获取RecyclerView的显示宽度
     */
    private fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }
}

class ItemViewInfo(
    var top: Int,
    var positionOffset: Float,
    var layoutPercent: Float
) {
    private var mIsBottom = false
    fun setIsBottom(): ItemViewInfo {
        mIsBottom = true
        return this
    }
}