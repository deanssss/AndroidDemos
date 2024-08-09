package xyz.dean.androiddemos.demos.dcalendar.internal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import xyz.dean.androiddemos.demos.dcalendar.afterByDay
import xyz.dean.androiddemos.demos.dcalendar.day
import xyz.dean.util.dp2pxF
import xyz.dean.util.sp2pxF
import java.util.Calendar

class CalendarMultiDateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val MAX_DATE_NUM = 7

    private val itemsPos = arrayOfNulls<Float>(MAX_DATE_NUM)
    private val datesNum = arrayOfNulls<Int>(MAX_DATE_NUM)
    private val numPaint: Paint

    private val textSizePx = context.sp2pxF(10f)
    private val textPaddingTop = context.dp2pxF(3f)
    private val textPaddingStart = context.dp2pxF(5f)

    private var num: Int = 1
    private var spacingPx = context.dp2pxF(1f)
    private var itemWidth: Int = 0

    init {
        numPaint = Paint().apply {
            color = Color.parseColor("#FF82A0CC")
            style = Paint.Style.FILL_AND_STROKE
            textSize = textSizePx
        }
    }

    fun setDateRange(start: Calendar, end: Calendar) {
        var i = 0
        val temp = start.clone() as Calendar
        while (!temp.afterByDay(end) && i < MAX_DATE_NUM) {
            datesNum[i++] = temp.day
            temp.add(Calendar.DAY_OF_MONTH, 1)
        }
        num = i
        invalidate()
    }

    fun setGridDimension(width: Int, height: Int, spacing: Float) {
        itemWidth = width
        spacingPx = spacing
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (itemWidth == 0) {
            itemWidth = ((measuredWidth - spacingPx * (num - 1)) / num).toInt()
        }
        itemsPos[0] = 0f
        for (i in 1 until num) {
            itemsPos[i] = i * (itemWidth + spacingPx)
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        for (i in 0 until num) {
            canvas.drawText("${datesNum[i] ?: "0"}",
                itemsPos[i]!! + textPaddingStart,
                textPaddingTop + textSizePx,
                numPaint)
        }
    }
}