package xyz.dean.androiddemos.demos.expandable_tag

import android.content.Context
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.View
import android.view.ViewGroup
import androidx.core.text.TextUtilsCompat
import androidx.core.view.children
import androidx.core.view.isGone
import java.util.Locale
import kotlin.math.max

class ExpandableFlowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {
    protected val allViews = mutableListOf<MutableList<View>>()
    protected val linesHeight = mutableListOf<Int>()
    protected val linesWidth = mutableListOf<Int>()

    private var lineViews = mutableListOf<View>()
    private var gravity: Int

    init {
        val layoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault())
        gravity = if (layoutDirection == LayoutDirection.RTL) GRAVITY_RIGHT else GRAVITY_LEFT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeW = MeasureSpec.getSize(widthMeasureSpec)
        val modeW = MeasureSpec.getMode(widthMeasureSpec)
        val sizeH = MeasureSpec.getSize(heightMeasureSpec)
        val modeH = MeasureSpec.getMode(heightMeasureSpec)

        var width = 0
        var height = 0

        var lineWidth = 0
        var lineHeight = 0

        val count = childCount

        children.forEachIndexed { index, child ->
            if (!child.isGone) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                val lp = child.layoutParams as MarginLayoutParams

                val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
                val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin

                if (lineWidth + childWidth > sizeW - paddingLeft - paddingRight) {
                    width = max(width, lineWidth)
                    lineWidth = childWidth
                    height += lineHeight
                    lineHeight = childHeight
                } else {
                    lineWidth += childWidth
                    lineHeight = max(lineHeight, childHeight)
                }
            }
            if (index == count - 1) {
                width = max(lineWidth, width)
                height += lineHeight
            }
        }
        setMeasuredDimension(
            if (modeW == MeasureSpec.EXACTLY) sizeW else width + paddingLeft + paddingRight,
            if (modeH == MeasureSpec.EXACTLY) sizeH else height + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        allViews.clear()
        linesHeight.clear()
        linesWidth.clear()
        lineViews.clear()

        val width = width

        var lineWidth = 0
        var lineHeight = 0

        children.filter { !it.isGone }.forEach { child ->
            val lp = child.layoutParams as MarginLayoutParams

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - paddingLeft - paddingRight) {
                linesHeight.add(lineHeight)
                allViews.add(lineViews)
                linesWidth.add(lineWidth)

                lineWidth = 0
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin
                lineViews = mutableListOf()
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin
            lineHeight = max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin)
            lineViews.add(child)
        }
        linesHeight.add(lineHeight)
        linesWidth.add(lineWidth)
        allViews.add(lineViews)

        var left = paddingLeft
        var top = paddingTop

        allViews.forEachIndexed { i, lineViews ->
            lineHeight = linesHeight[i]
            lineWidth = linesWidth[i]
            left = when (gravity) {
                GRAVITY_LEFT -> paddingLeft
                GRAVITY_CENTER -> (width - lineWidth) / 2 + paddingLeft
                GRAVITY_RIGHT -> width - (lineWidth + paddingLeft) - paddingRight
                else -> paddingLeft
            }
            if (gravity == GRAVITY_RIGHT) {
                lineViews.reverse()
            }

            lineViews.filter { !it.isGone }.forEach { child ->
                val lp = child.layoutParams as MarginLayoutParams

                val lc = left + lp.leftMargin
                val tc = top + lp.topMargin
                val rc = lc + child.measuredWidth
                val bc = tc + child.measuredHeight
                child.layout(lc, tc, rc, bc)

                left += child.measuredWidth + lp.leftMargin + lp.rightMargin
            }
            top += lineHeight
        }
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