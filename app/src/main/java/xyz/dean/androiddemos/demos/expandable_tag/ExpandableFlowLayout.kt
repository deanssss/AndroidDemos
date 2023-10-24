package xyz.dean.androiddemos.demos.expandable_tag

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.TextUtilsCompat
import androidx.core.view.isGone
import java.util.Locale
import kotlin.math.max

/**
 * 可展开的FlowLayout，支持设定折叠时的最大行数
 */
class ExpandableFlowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle), OnDataChangedListener {

    var onItemClickListener: ((position: Int) -> Unit)? = null
    var isExpand = false
        set(value) {
            field = value
            requestLayout()
        }

    private val allViews = MutableList<MutableList<View>>(LINES_VIEW_INIT_SIZE) { mutableListOf() }
    private val linesHeight = mutableListOf<Int>()
    private val linesWidth = mutableListOf<Int>()
    private var lineViews = mutableListOf<View>()

    private var gravity: Int
    private val expandIcon: View
    private var adapter: Adapter<*>? = null

    init {
        val layoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault())
        gravity = if (layoutDirection == LayoutDirection.RTL) GRAVITY_RIGHT else GRAVITY_LEFT
        expandIcon = createExpandIcon()
    }

    fun setAdapter(adapter: Adapter<*>) {
        adapter.setOnDataChangedListener(this)
        this.adapter = adapter
        updateView()
    }

    private fun updateView() {
        removeAllViews()
        val adapter = adapter ?: return
        for (i in 0 until adapter.count()) {
            val itemView = adapter.getView(this, i)
            itemView.setOnClickListener {
                onItemClickListener?.invoke(i)
            }
            addView(itemView)
        }

        if (expandIcon.parent != null) {
            (expandIcon.parent as? ViewGroup)?.removeView(expandIcon)
        }
        addView(expandIcon)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeW = MeasureSpec.getSize(widthMeasureSpec)
        val modeW = MeasureSpec.getMode(widthMeasureSpec)
        val sizeH = MeasureSpec.getSize(heightMeasureSpec)
        val modeH = MeasureSpec.getMode(heightMeasureSpec)

        allViews.forEach { it.clear() }
        linesHeight.clear()
        linesWidth.clear()

        var lineWidth = 0
        var lineHeight = 0
        var measuredWidth = sizeW
        var measuredHeight = sizeH

        val layoutWidth = measuredWidth - paddingLeft - paddingRight
        var lineNum = 1
        var skipLatest = false
        lineViews = getOrCreateLineViews(lineNum)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.isGone || child == expandIcon) continue

            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as MarginLayoutParams
            val childRangeW = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childRangeH = child.measuredHeight + lp.topMargin + lp.bottomMargin

            if (childRangeW + lineWidth > layoutWidth) {
                // 当前行已满，保存测量记录和View分组
                if (!isExpand && lineNum >= MAX_LINES_IN_COLLAPSED) {
                    // 如果处于折叠状态下，布局到最大行数时，跳过剩余子View的布局
                    skipLatest = true
                    if (layoutWidth - lineWidth > 100) {
                        // 若剩余宽度大于50px，则多显示一个view，即使会被截断
                        lineWidth += childRangeW
                        lineHeight = max(lineHeight, childRangeH)
                        lineViews.add(child)
                    }
                }
                linesHeight.add(lineHeight)
                linesWidth.add(lineWidth)

                // 清除数据，开始测量下一行
                lineNum++
                lineWidth = 0
                lineHeight = childRangeH
                lineViews = getOrCreateLineViews(lineNum)
            }
            if (skipLatest) break
            lineWidth += childRangeW
            lineHeight = max(lineHeight, childRangeH)
            lineViews.add(child)
        }
        if (!skipLatest) {
            // 添加最后一行，如果是跳过剩余子View时，则不添加，因为跳过时就已经确定好最后一行了。
            linesHeight.add(lineHeight)
            linesWidth.add(lineWidth)
        }

        // 处理展开icon
        measureChild(expandIcon, widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(linesHeight.lastOrNull() ?: 50, MeasureSpec.EXACTLY))

        if (modeW != MeasureSpec.EXACTLY) {
            measuredWidth = (linesWidth.maxOrNull() ?: sizeW) + paddingLeft + paddingRight
        }
        if (modeH != MeasureSpec.EXACTLY) {
            measuredHeight = linesHeight.sum() + paddingTop + paddingBottom
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun getOrCreateLineViews(lineNum: Int): MutableList<View> {
        var views = allViews.getOrNull(lineNum - 1)
        if (views == null) {
            views = mutableListOf()
            allViews.add(views)
        }
        return views
    }

    private fun createExpandIcon(): View = TextView(context).apply {
        setBackgroundColor(Color.parseColor("#ff333333"))
        setTextColor(Color.parseColor("#ffffffff"))
        text = if (isExpand) "^" else "v"
        setPadding(50, 24, 50, 24)
        setOnClickListener {
            isExpand = !isExpand
            text = if (isExpand) "^" else "v"
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var lineWidth = 0
        var lineHeight = 0
        var layoutLeft: Int
        var layoutTop = paddingTop
        val width = width
        allViews.forEachIndexed { i, lineViews ->
            if (lineViews.isEmpty()) return@forEachIndexed
            lineHeight = linesHeight[i]
            lineWidth = linesWidth[i]
            layoutLeft = when (gravity) {
                GRAVITY_LEFT -> paddingLeft
                GRAVITY_CENTER -> (width - lineWidth) / 2 + paddingLeft
                GRAVITY_RIGHT -> width - (lineWidth + paddingLeft) - paddingRight
                else -> paddingLeft
            }
            if (gravity == GRAVITY_RIGHT) lineViews.reverse()

            lineViews.forEach { child ->
                val lp = child.layoutParams as MarginLayoutParams
                val lc = layoutLeft + lp.leftMargin
                val tc = layoutTop + lp.topMargin
                val rc = lc + child.measuredWidth
                val bc = tc + child.measuredHeight
                child.layout(lc, tc, rc, bc)
                layoutLeft += child.measuredWidth + lp.leftMargin + lp.rightMargin
            }
            layoutTop += lineHeight
        }

        // 处理展开icon
        expandIcon.layout(
            width - paddingRight - expandIcon.measuredWidth,
            layoutTop - lineHeight,
            width - paddingRight,
            layoutTop + expandIcon.measuredHeight)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onDataChanged() {
        updateView()
    }

    abstract class Adapter<T> constructor(
        private var data: List<T>
    ) {
        private var mOnDataChangedListener: OnDataChangedListener? = null

        fun count(): Int = data.size

        fun getItem(position: Int): T? = data.getOrNull(position)

        abstract fun getView(parent: ExpandableFlowLayout, position: Int): View

        fun setOnDataChangedListener(listener: OnDataChangedListener?) {
            mOnDataChangedListener = listener
        }

        fun notifyDataChanged() {
            mOnDataChangedListener?.onDataChanged()
        }
    }

    companion object {
        private const val GRAVITY_LEFT = -1
        private const val GRAVITY_CENTER = 0
        private const val GRAVITY_RIGHT = 1

        private const val LINES_VIEW_INIT_SIZE = 5
        private const val  MAX_LINES_IN_COLLAPSED= 2

    }
}

interface OnDataChangedListener {
    fun onDataChanged()
}