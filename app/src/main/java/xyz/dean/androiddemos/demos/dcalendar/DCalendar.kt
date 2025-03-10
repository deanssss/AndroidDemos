package xyz.dean.androiddemos.demos.dcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.demos.dcalendar.internal.CalendarPagerAdapter
import java.util.*

class DCalendar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : FrameLayout(context, attrs)
{
    private lateinit var pager: ViewPager
    private val pagerAdapter = CalendarPagerAdapter()
    private var currentSelectDate = Calendar.getInstance()

    init {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.d_calendar_layout, this, false)
        addView(view)
        initView()
    }

    private fun initView() {
        pager = findViewById(R.id.calendar_pager)
        pager.adapter = pagerAdapter
        val dateTv = findViewById<TextView>(R.id.year_month_tv)
        pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageSelected(position: Int) {
                val yearMonth = pagerAdapter.getYearMonth(position)
                dateTv.text = "${yearMonth.year}年${yearMonth.month + 1}月"
            }
        })
        val prevIv = findViewById<ImageView>(R.id.prev_month_iv)
        prevIv.setOnClickListener {
            val current = pager.currentItem
            pager.currentItem = current - 1
        }
        val nextIv = findViewById<ImageView>(R.id.next_month_iv)
        nextIv.setOnClickListener {
            val current = pager.currentItem
            pager.currentItem = current + 1
        }
    }

    fun setData(
        start: Calendar? = null,
        end: Calendar? = null,
        asyncDataProvider: (year: Int, month: Int, (Any) -> Unit) -> Unit = { _, _, _ -> }
    ) {
        if (start != null && end != null && start.after(end)) {
            error("Start date cannot be later than end date.")
        }
        pagerAdapter.startDate = start ?: defaultStart
        pagerAdapter.endDate = end ?: defaultEnd
        pagerAdapter.asyncDataProvider = asyncDataProvider
        pagerAdapter.notifyDataSetChanged()

        if (currentSelectDate.before(pagerAdapter.startDate)
            || currentSelectDate.after(pagerAdapter.endDate)
        ) {
            currentSelectDate = pagerAdapter.startDate
        }
        pager.setCurrentItem(pagerAdapter.getPagePos(currentSelectDate), false)
    }

    fun rollTo(date: Calendar, withAnim: Boolean = false) {
        currentSelectDate = date
        val pagePos = pagerAdapter.getPagePos(currentSelectDate)
        pager.setCurrentItem(pagePos, withAnim)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val modeW = MeasureSpec.getMode(widthMeasureSpec)
        val sizeH = MeasureSpec.getSize(heightMeasureSpec)
        val modeH = MeasureSpec.getMode(heightMeasureSpec)

        if (modeH == MeasureSpec.AT_MOST) {
            val width = (CALENDAR_VIEW_SIZE_RATIO * sizeH).toInt()
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, modeW), heightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    companion object {
        private const val CALENDAR_VIEW_SIZE_RATIO = 950 / 688f

        inline val defaultStart: Calendar get() = Calendar.getInstance()
            .apply { clear(); set(1900, 0, 1) }
        inline val defaultEnd: Calendar get() = Calendar.getInstance()
            .apply { clear(); set(2900, 0, 1) }
    }
}