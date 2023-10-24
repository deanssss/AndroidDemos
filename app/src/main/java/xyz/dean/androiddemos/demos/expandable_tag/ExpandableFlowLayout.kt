package xyz.dean.androiddemos.demos.expandable_tag

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.TextUtilsCompat
import androidx.core.view.children
import androidx.core.view.isGone
import java.util.Locale
import kotlin.math.max

open class ExpandableFlowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {
    protected val allViews = mutableListOf<MutableList<View>>()
    protected val linesHeight = mutableListOf<Int>()
    protected val linesWidth = mutableListOf<Int>()

    private var lineViews = mutableListOf<View>()
    private var gravity: Int

    private var minColumns = 2
    var isExpand = false
        set(value) {
            field = value
            requestLayout()
        }
    private val expandIcon: View

    init {
        val layoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault())
        gravity = if (layoutDirection == LayoutDirection.RTL) GRAVITY_RIGHT else GRAVITY_LEFT
        expandIcon = makeAndAddExpandIcon()
    }

    fun setAdapter() {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeW = MeasureSpec.getSize(widthMeasureSpec)
        val modeW = MeasureSpec.getMode(widthMeasureSpec)
        val sizeH = MeasureSpec.getSize(heightMeasureSpec)
        val modeH = MeasureSpec.getMode(heightMeasureSpec)

        allViews.clear()
        lineViews.clear()
        linesHeight.clear()
        linesWidth.clear()

        var lineWidth = 0
        var lineHeight = 0
        var measuredWidth = sizeW
        var measuredHeight = sizeH

        val layoutWidth = measuredWidth - paddingLeft - paddingRight
        var lineNum = 1
        var skipLatest = false

        for (child in children.filter { !it.isGone && it != expandIcon }) {
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as MarginLayoutParams
            val childRangeW = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childRangeH = child.measuredHeight + lp.topMargin + lp.bottomMargin

            if (childRangeW + lineWidth > layoutWidth) {
                // 当前行已满，保存测量记录和View分组
                if (!isExpand && lineNum >= minColumns) {
                    // 如果处于折叠状态下，布局到最小行数行时，跳过剩余子View的布局
                    skipLatest = true
                    if (layoutWidth - lineWidth > 100) {
                        // 剩余宽度大于50px，多显示一个view，即使会被截断
                        lineWidth += childRangeW
                        lineHeight = max(lineHeight, childRangeH)
                        lineViews.add(child)
                    }
                }
                linesHeight.add(lineHeight)
                allViews.add(lineViews)
                linesWidth.add(lineWidth)

                // 清除数据，开始测量下一行
                lineNum++
                lineWidth = 0
                lineHeight = childRangeH
                lineViews = mutableListOf()
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
            allViews.add(lineViews)
        }

        // 处理展开icon
        if (expandIcon.parent != null) {
            (expandIcon.parent as? ViewGroup)?.removeView(expandIcon)
        }
        addView(expandIcon)
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

    private fun makeAndAddExpandIcon(): View {
        val expandIcon = TextView(context).apply {
            setBackgroundColor(Color.parseColor("#ff333333"))
            setTextColor(Color.parseColor("#ffffffff"))
            text = if (isExpand) "^" else "v"
            setPadding(50, 24, 50, 24)
            setOnClickListener {
                isExpand = !isExpand
                text = if (isExpand) "^" else "v"
            }
        }
//        addView(expandIcon)
        return expandIcon
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var lineWidth = 0
        var lineHeight = 0
        var layoutLeft: Int
        var layoutTop = paddingTop
        val width = width
        allViews.forEachIndexed { i, lineViews ->
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
        layoutTop -= lineHeight
        layoutLeft = width - paddingRight - expandIcon.measuredWidth
        expandIcon.layout(layoutLeft, layoutTop, layoutLeft + expandIcon.measuredWidth, layoutTop + expandIcon.measuredHeight)
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

    companion object {
        private val TAG = "ExpandableFlowLayout"

        private const val GRAVITY_LEFT = -1
        private const val GRAVITY_CENTER = 0
        private const val GRAVITY_RIGHT = 1
    }
}