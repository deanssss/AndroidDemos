package xyz.dean.androiddemos.demos.dcalendar.internal

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.demos.dcalendar.day
import xyz.dean.androiddemos.demos.dcalendar.month

sealed class CalendarGridViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    abstract fun renderCalendarData(item: CalendarGridItem)
    abstract fun setGridDimension(width: Int, height: Int, spacing: Float)
    abstract fun bindGridEvent(item: CalendarGridItem)

    class NormalCalendarGridViewHolder(rootView: View) : CalendarGridViewHolder(rootView) {
        private val dayTv: TextView = rootView.findViewById(R.id.day_of_month_tv)

        override fun renderCalendarData(item: CalendarGridItem) {
            if (item !is CalendarGridItem.NormalGridItem) return
            dayTv.text = "${item.date.day}"
        }

        override fun setGridDimension(width: Int, height: Int, spacing: Float) {
            itemView.layoutParams.height = height
        }

        override fun bindGridEvent(item: CalendarGridItem) {
        }
    }

    class TaskCalendarGridViewHolder(rootView: View) : CalendarGridViewHolder(rootView) {
        private val dateView: CalendarMultiDateView = rootView.findViewById(R.id.date_view)
        private val taskImageIv: ImageView = rootView.findViewById(R.id.task_image_iv)
        private val teamImageIv: ImageView = rootView.findViewById(R.id.team_image_iv)
        private val teamIconIv: ImageView = rootView.findViewById(R.id.team_icon_iv)
        private val occupiedTv: TextView = rootView.findViewById(R.id.occupied_tv)

        override fun renderCalendarData(item: CalendarGridItem) {
            if (item !is CalendarGridItem.TaskGridItem) return
            dateView.setDateRange(item.dateStart, item.dateEnd)
        }

        override fun setGridDimension(width: Int, height: Int, spacing: Float) {
            itemView.layoutParams.height = height

            taskImageIv.layoutParams.apply {
                this.width = (width + spacing).toInt()
            }
            teamImageIv.layoutParams.apply {
                this.width = width
            }
            dateView.setGridDimension(width, height, spacing)
        }

        override fun bindGridEvent(item: CalendarGridItem) {
            if (item !is CalendarGridItem.TaskGridItem) return

            itemView.setOnClickListener {
                Log.d("DDDDD", "${item.dateStart.month + 1}-${item.dateStart.day} ----> ${item.dateEnd.month + 1}-${item.dateEnd.day}, days: ${item.days}")
            }
        }
    }

    companion object {
        fun createViewHolder(
            layoutInflater: LayoutInflater,
            parent: ViewGroup, itemType: Int
        ): CalendarGridViewHolder = when (itemType) {
            1 -> TaskCalendarGridViewHolder(
                layoutInflater.inflate(R.layout.calendar_item_task_layout, parent, false))
            else -> NormalCalendarGridViewHolder(
                layoutInflater.inflate(R.layout.calendar_item_normal_layout, parent, false))
        }
    }
}