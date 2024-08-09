package xyz.dean.androiddemos.demos.dcalendar.internal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.demos.dcalendar.*
import xyz.dean.util.dp2pxF
import java.util.Calendar

class CalendarGridView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    private val gridSpacing = context.dp2pxF(1f)
    private val maskDecoration = CalendarMaskDecoration()
    private val adapter = CalendarGridAdapter()

    private val shadowPaint: Paint = Paint().apply {
        color = Color.parseColor("#4D2C4669")
        style = Paint.Style.FILL_AND_STROKE
    }

    private val highlightBorderColor = Color.parseColor("#FF73AEFE")
    private val highlightFillColor = Color.parseColor("#4D4779BE")
    private val highlightPaint: Paint = Paint()

    init {
        itemAnimator = null
        setAdapter(adapter)
        layoutManager = GridLayoutManager(context, NUM_OF_COLUMN).apply {
            spanSizeLookup = createSpanSizeLookup()
        }
        addItemDecoration(maskDecoration)
        addItemDecoration(
            // item之间增加间隔，外边框也加一半间隔的边距
            GridSpaceItemDecoration(gridSpacing.toInt(), true)
        )
    }

    fun setDate(yearMonth: Calendar) {
        adapter.setDate(yearMonth)
        setMaskDecorations(yearMonth)
    }

    fun setData() {
        adapter.setData()
        setMaskDecorations(adapter.yearMonth)
    }

    private fun setMaskDecorations(yearMonth: Calendar) {
        maskDecoration.clearAll()
        // 日历开始非本月的遮罩
        val fstDayInMonthView = yearMonth.firstDayInMonthView()
        if (fstDayInMonthView.month != yearMonth.month) {
            val startShadowSpan = yearMonth.dayDiff(fstDayInMonthView)
            maskDecoration.addMask(MaskItemInfo(0, 0, startShadowSpan, this::drawShadow))
        }

        // 日历结束非本月的遮罩
        val lstDayOfMonth = yearMonth.lastDayOfMonth()
        val lstDayInMonthView = yearMonth.lastDayInMonthView()
        if (lstDayInMonthView.month != yearMonth.month) {
            val endShadowSpan = lstDayInMonthView.dayDiff(lstDayOfMonth)
            val firstDayOfNextMonth = lstDayInMonthView.firstDayOfMonth()
            val pos = adapter.findDateInAdapterPos(firstDayOfNextMonth)
            if (pos != -1) {
                val data = adapter.getData(pos)
                val offset = firstDayOfNextMonth.week - data.startWeek
                maskDecoration.addMask(MaskItemInfo(pos, offset, endShadowSpan, this::drawShadow))
            }
        }

        // 日历当日高亮
        val now = Calendar.getInstance()
        val nowPos = adapter.findDateInAdapterPos(now)
        if (nowPos != -1) {
            val data = adapter.getData(nowPos)
            val offset = now.week - data.startWeek
            maskDecoration.addMask(MaskItemInfo(nowPos, offset, 1, this::drawHighLight))
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (measuredWidth != 0 && measuredHeight != 0) {
            // 计算格子宽高
            val gridHeight = ((measuredHeight - gridSpacing * NUM_OF_ROW - HALF_GRID_HEIGH) / NUM_OF_ROW).toInt()
            val gridWidth = ((measuredWidth - gridSpacing * NUM_OF_COLUMN) / NUM_OF_COLUMN).toInt()
            if (gridHeight != 0 && gridWidth != 0) {
                adapter.setGridDimension(gridWidth, gridHeight, gridSpacing)
                maskDecoration.setGridDimension(gridWidth, gridHeight, gridSpacing)
            }
        }
    }

    private fun drawShadow(
        maskItemInfo: MaskItemInfo, canvas: Canvas,
        l: Float, t: Float, r: Float, b: Float
    ) {
        canvas.drawRect(l, t, r, b, shadowPaint)
    }

    private fun drawHighLight(
        maskItemInfo: MaskItemInfo, canvas: Canvas,
        l: Float, t: Float, r: Float, b: Float
    ) {
        highlightPaint.style = Paint.Style.FILL
        highlightPaint.color = highlightFillColor
        canvas.drawRect(l, t, r, b, highlightPaint)
        highlightPaint.style = Paint.Style.STROKE
        highlightPaint.color = highlightBorderColor
        canvas.drawRect(l, t, r, b, highlightPaint)
    }

    private fun createSpanSizeLookup(): SpanSizeLookup = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            val data = adapter.getData(position)
            return data.days
        }
        override fun getSpanIndex(position: Int, spanCount: Int): Int {
            // 重写一下，使用adapter中的数据加快计算
            val data = adapter.getData(position)
            return data.startWeek - 1
        }
    }

    companion object {
        // 超过5行日历，多显示半行
        private val HALF_GRID_HEIGH = 40
        // 日历行数（计算用）
        private val NUM_OF_ROW = 5
        // 日历列数（计算用）
        private val NUM_OF_COLUMN = 7
    }
}