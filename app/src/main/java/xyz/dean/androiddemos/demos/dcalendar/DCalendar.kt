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
import java.util.Calendar

class DCalendar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : FrameLayout(context, attrs)
{
    private lateinit var pager: ViewPager
    private val pagerAdapter = CalendarPagerAdapter()

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
                dateTv.text = pagerAdapter.getYearMonthStr(position)
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
        setStartAndEnd()
        377 / 234
    }

    fun setStartAndEnd(start: Calendar? = null, end: Calendar? = null) {
        pagerAdapter.startDate = start ?: defaultStart
        pagerAdapter.endDate = end ?: defaultEnd
        pagerAdapter.notifyDataSetChanged()
    }

    fun rollTo(date: Calendar) {
        pager.currentItem = pagerAdapter.getPagePos(date)
    }

    companion object {
        inline val defaultStart: Calendar get() = Calendar.getInstance().apply {
            clear()
            set(1900, 0, 1)
        }
        inline val defaultEnd: Calendar get() = Calendar.getInstance().apply {
            clear()
            set(2900, 0, 1)
        }
    }
}