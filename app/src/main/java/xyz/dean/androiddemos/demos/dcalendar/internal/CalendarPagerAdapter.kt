package xyz.dean.androiddemos.demos.dcalendar.internal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains
import androidx.viewpager.widget.PagerAdapter
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.demos.dcalendar.*
import java.util.*

internal class CalendarPagerAdapter : PagerAdapter() {
    var startDate: Calendar = DCalendar.defaultStart
        set(value) { field = value.firstDayOfMonth() }
    var endDate: Calendar = DCalendar.defaultEnd
        set(value) { field = value.firstDayOfMonth() }
    var asyncDataProvider: (year: Int, month: Int, (Any) -> Unit) -> Unit =
        { _, _, _ -> }

    private val views: Array<View?> = arrayOfNulls(CACHE_VIEW_COUNT)

    fun getYearMonthStr(position: Int): String {
        val date = getYearMonth(position)
        return "${date.year}年${date.month + 1}月"
    }

    fun getYearMonth(position: Int): Calendar {
        if (position < 0 || position > count) outOfRange()
        return startDate.next(Calendar.MONTH, position)
    }

    override fun getCount(): Int = endDate.monthDiff(startDate) + 1

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position % CACHE_VIEW_COUNT] ?: kotlin.run {
            LayoutInflater.from(container.context)
                .inflate(R.layout.month_calendar_layout, container, false)
                .apply {
                    initView(this)
                    views[position % CACHE_VIEW_COUNT] = this
                }
        }

        if (container.contains(view)) container.removeView(view)
        val yearMonthStr = getYearMonthStr(position)
        view.tag = yearMonthStr
        container.addView(view)

        val yearMonth = getYearMonth(position)
        setViewData(view, yearMonth, position)
        return yearMonthStr
    }

    private fun initView(view: View) {
        val gridView = view.findViewById<AdaptiveGridView>(R.id.gridview)
        val adapter = DailyViewGridAdapter()
        gridView.adapter = adapter
        gridView.numColumns = CALENDAR_GRID_COLUMNS
    }

    private fun setViewData(view: View, yearMonth: Calendar, position: Int) {
        val gridView = view.findViewById<AdaptiveGridView>(R.id.gridview)
        val adapter = gridView.adapter as? DailyViewGridAdapter ?: return
        adapter.setDate(yearMonth)

        asyncDataProvider(yearMonth.year, yearMonth.month + 1) {
            val isPageDestroyed = !views.any { v -> v?.tag == getYearMonthStr(position) }
            if (isPageDestroyed) {
                // 异步数据返回时，page可能已经被回收了，此时不要执行渲染
                return@asyncDataProvider
            }
            adapter.setData(it)
        }
    }

    override fun isViewFromObject(view: View, obj: Any) = obj == view.tag

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = container.findViewWithTag<View>(obj)
        view.tag = ""
        container.removeView(view)
    }

    fun getPagePos(yearMonth: Calendar): Int {
        if (yearMonth.before(startDate) || yearMonth.after(endDate)) outOfRange()
        return startDate.monthDiff(yearMonth)
    }

    private fun outOfRange(): Nothing =  error("out of time range: " +
            "${startDate.year}-${startDate.month} " + "~ ${endDate.year}-${endDate.month}")

    companion object {
        private const val CACHE_VIEW_COUNT = 4
        private const val CALENDAR_GRID_COLUMNS = 7
    }
}