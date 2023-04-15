package xyz.dean.androiddemos.demos.dcalendar.internal

import xyz.dean.androiddemos.demos.dcalendar.week
import java.util.Calendar

sealed class CalendarGridItem(
    val typeValue: Int,
    val days: Int
) {
    abstract val startWeek: Int

    class NormalGridItem(
        val date: Calendar
    ) : CalendarGridItem(0, 1) {
        override val startWeek: Int  = date.week
    }

    class TaskGridItem(
        val dateStart: Calendar,
        val dateEnd: Calendar,
        days: Int
    ) : CalendarGridItem(1, days) {
        override val startWeek: Int = dateStart.week
    }
}