package xyz.dean.androiddemos.demos.dcalendar.internal

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.demos.dcalendar.*
import java.util.*

internal class DailyViewGridAdapter : BaseAdapter() {
    private var dataList: List<DailyItem> = emptyList()
    private var yearMonth = Calendar.getInstance().toDailyDate()
    private var now = Calendar.getInstance().toDailyDate()

    override fun getCount(): Int = dataList.size
    override fun getItem(position: Int): Any = dataList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: kotlin.run {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.daily_item_layout, parent, false)
        }
        val dailyData = getItem(position) as DailyItem
        val dayTv = view.findViewById<TextView>(R.id.day_of_month_tv)
        dayTv.text = dailyData.date.day.toString()
        val bg = view.findViewById<ImageView>(R.id.occupied_team_iv)
        if (dailyData.date.lateThan(now)) {
            bg.setBackgroundColor(Color.parseColor("#803D6AA8"))
        } else {
            bg.setBackgroundColor(Color.parseColor("#4D2C4669"))
        }

        val mask = view.findViewById<View>(R.id.mask_view)
        // 日期是否是本月
        mask.isVisible = dailyData.date.month != yearMonth.month

        val name = view.findViewById<TextView>(R.id.activity_name_tv)
        if (Random().nextInt(10) == 1) name.text = dailyData.data

        return view
    }

    fun setDate(yearMonth: Calendar) {
        val fstDay = yearMonth.firstDayInMonthView()
        val lstDay = yearMonth.lastDayInMonthView()
        val list = mutableListOf<DailyItem>()
        do {
            list.add(DailyItem(fstDay.toDailyDate()))
            fstDay.add(Calendar.DAY_OF_MONTH, 1)
        } while (fstDay.before(lstDay))
        list.add(DailyItem(lstDay.toDailyDate()))
        this.dataList = list
        this.yearMonth = yearMonth.toDailyDate()
        notifyDataSetChanged()
    }

    fun setData(data: Any) {
        Log.d("DDDD", "received data: $data")
        this.dataList = this.dataList.map { it.copy(data = "$data") }
        notifyDataSetChanged()
    }

    data class DailyItem(
        val date: DailyDate,
        val data: String = "",
    )

    data class DailyDate(
        val year: Int,
        val month: Int,
        val day: Int,
        val week: Int,
        val dayOfYear: Int,
    )

    private fun Calendar.toDailyDate(): DailyDate =
        DailyDate(year, month + 1, day, week, dayOfYear)

    private fun DailyDate.lateThan(date: DailyDate): Boolean {
        return if (this.year < date.year) false
        else !(this.year == date.year && this.dayOfYear <= date.dayOfYear)
    }
}