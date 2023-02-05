package xyz.dean.androiddemos.demos.dcalendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import xyz.dean.androiddemos.R
import java.util.*

class DailyViewGridAdapter : BaseAdapter() {
    private var data: List<DailyItem> = emptyList()
    private var date = Calendar.getInstance()
    private var now = Calendar.getInstance()

    fun setDate(date: Calendar, data: Any?) {
        val fstDay = date.firstDayInMonthView()
        val lstDay = date.lastDayInMonthView()
        val list = mutableListOf<DailyItem>()
        do {
            list.add(fstDay.toDailyItem())
            fstDay.add(Calendar.DAY_OF_MONTH, 1)
        } while (fstDay.before(lstDay))
        list.add(lstDay.toDailyItem())
        this.data = list
        this.date = date
        notifyDataSetChanged()
    }

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: kotlin.run {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.daily_item_layout, parent, false)
        }
        val data = getItem(position) as DailyItem
        val dayTv = view.findViewById<TextView>(R.id.day_of_month_tv)
        dayTv.text = data.day.toString()
        val bg = view.findViewById<ImageView>(R.id.occupied_team_iv)
        if (data.dayOfYear <= now.dayOfYear) {
            bg.setBackgroundColor(Color.parseColor("#4D2C4669"))
        } else {
            bg.setBackgroundColor(Color.parseColor("#803D6AA8"))
        }

        val mask = view.findViewById<View>(R.id.mask_view)
        mask.isVisible = data.month != date.month + 1

        return view
    }

    data class DailyItem(
        val year: Int,
        val month: Int,
        val day: Int,
        val week: Int,
        val dayOfYear: Int,
    )

    private fun Calendar.toDailyItem(): DailyItem {
        return DailyItem(year, month + 1, day, week, dayOfYear)
    }
}