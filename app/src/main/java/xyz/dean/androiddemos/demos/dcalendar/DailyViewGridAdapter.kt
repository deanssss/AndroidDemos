package xyz.dean.androiddemos.demos.dcalendar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import xyz.dean.androiddemos.R
import xyz.dean.util.dp2px
import java.util.*

class DailyViewGridAdapter(context: Context) : BaseAdapter() {
    private val spacing = context.dp2px(1f)

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

        if (width != 0 && height != 0) {
            val lp = view.layoutParams
            lp.width = width
            lp.height = height
        }

        return view
    }

    private var width = 0
    private var height = 0

    fun refreshLayout(w: Int, h: Int) {
        // 多减50是为了预留多半排显示
        height = ((h - spacing * 4 - 50) / 5f).toInt()
        width = ((w - spacing * 6) / 7f).toInt() // height * (53 / 46f)
        notifyDataSetChanged()
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