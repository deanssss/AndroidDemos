package xyz.dean.androiddemos.demos.dcalendar.internal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.demos.dcalendar.*
import java.util.Calendar
import kotlin.random.Random

class CalendarGridAdapter : RecyclerView.Adapter<CalendarGridViewHolder>() {
    private var gridWidth: Int = 0
    private var gridHeight: Int = 0
    private var gridSpacing: Float = 1f
    private var dataList: List<CalendarGridItem> = emptyList()
    private var isLoading: Boolean = true

    var yearMonth: Calendar = Calendar.getInstance()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): CalendarGridViewHolder {
        return CalendarGridViewHolder.createViewHolder(
            LayoutInflater.from(parent.context), parent, itemType)
    }

    override fun getItemCount(): Int = if (isLoading) 35 else dataList.size
    override fun getItemViewType(position: Int): Int = dataList[position].typeValue
    fun getData(position: Int): CalendarGridItem = dataList[position]

    override fun onBindViewHolder(holder: CalendarGridViewHolder, position: Int) {
        if (isLoading) return
        holder.renderCalendarData(dataList[position])
        holder.bindGridEvent(dataList[position])
        if (gridHeight != 0 && gridWidth != 0) {
            holder.setGridDimension(gridWidth, gridHeight, gridSpacing)
        }
    }

    fun setGridDimension(width: Int, height: Int, spacing: Float) {
        gridWidth = width
        gridHeight = height
        gridSpacing = spacing
    }

    fun setDate(yearMonth: Calendar) {
        var fstDay = yearMonth.firstDayInMonthView()
        val lstDay = yearMonth.lastDayInMonthView()
        val list = mutableListOf<CalendarGridItem>()
        val random = Random(System.currentTimeMillis())
        do {
            if (random.nextInt(0, 10) < 2) {
                val days = random.nextInt(1, 14)
                if (days == 1) {
                    list.add(CalendarGridItem.TaskGridItem(fstDay, fstDay, 1))
                    fstDay = fstDay.next(Calendar.DAY_OF_MONTH, 1)
                    continue
                }

                var start = fstDay.next(Calendar.DAY_OF_MONTH, 0)
                // 如果任务的最后一天不超过日历的最后一天，直接使用任务的最后一天作为结束日期，
                // 反之则使用日历最后一天作为结束日。
                val end = fstDay.next(Calendar.DAY_OF_MONTH, days - 1)
                    .let { if (it.afterByDay(lstDay)) lstDay else it }
                do {
                    // 遇到周最后一天或者结束日，就创建一个task item
                    if (start.week == Calendar.SATURDAY || start.isSameDay(end)) {
                        val duration = start.dayDiff(fstDay) + 1
                        list.add(CalendarGridItem.TaskGridItem(fstDay, start, duration))
                        fstDay = fstDay.next(Calendar.DAY_OF_MONTH, duration)
                    }
                    start = start.next(Calendar.DAY_OF_MONTH, 1)
                } while (!start.afterByDay(end))
            } else {
                list.add(CalendarGridItem.NormalGridItem(fstDay))
                fstDay = fstDay.next(Calendar.DAY_OF_MONTH, 1)
            }
        } while (!fstDay.afterByDay(lstDay))
        this.dataList = list
        this.yearMonth = yearMonth
        this.isLoading = false
        notifyDataSetChanged()
    }

    fun setData() {
    }

    fun showLoading() {
        dataList = emptyList()
        isLoading = true
        notifyDataSetChanged()
    }

    fun findDateInAdapterPos(date: Calendar): Int {
        // 日期不在日历显示范围内，直接返回-1
        val fstDay = yearMonth.firstDayInMonthView()
        val lstDay = yearMonth.lastDayInMonthView()
        if (date.beforeByDay(fstDay) || date.afterByDay(lstDay)) return -1

        return dataList.indexOfFirst {
            when (it) {
                is CalendarGridItem.NormalGridItem -> it.date.isSameDay(date)
                is CalendarGridItem.TaskGridItem ->
                    !date.beforeByDay(it.dateStart) && !date.afterByDay(it.dateEnd)
            }
        }
    }

    companion object {
        private val TAG = "DailyGridAdapter"
    }
}