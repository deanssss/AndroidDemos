package xyz.dean.androiddemos.demos.dcalendar

import java.util.*

inline val Calendar.year get() = get(Calendar.YEAR)
inline val Calendar.month get() = get(Calendar.MONTH)
inline val Calendar.day get() = get(Calendar.DAY_OF_MONTH)
inline val Calendar.week get() = get(Calendar.DAY_OF_WEEK)
inline val Calendar.dayOfYear get() = get(Calendar.DAY_OF_YEAR)

fun Calendar.firstDayOfMonth(): Calendar {
    val new = Calendar.getInstance()
    new.clear()
    new.set(year, month, 1)
    return new
}

fun Calendar.prev(field: Int, amount: Int): Calendar {
    val new = Calendar.getInstance()
    new.clear()
    new.set(year, month, day)
    new.add(field, -amount)
    return new
}

fun Calendar.next(field: Int, amount: Int): Calendar {
    val new = Calendar.getInstance()
    new.clear()
    new.set(year, month, day)
    new.add(field, amount)
    return new
}

fun Calendar.firstDayInMonthView(): Calendar {
    val new = Calendar.getInstance()
    new.clear()
    new.set(year, month, 1)
    val roll = new.week - 1
    if (roll > 0) new.add(Calendar.DAY_OF_MONTH, -roll)
    return new
}

fun Calendar.lastDayInMonthView(): Calendar {
    val new = Calendar.getInstance()
    new.clear()
    new.set(year, month, 1)
    new.add(Calendar.MONTH, 1)
    new.add(Calendar.DAY_OF_MONTH, -1)
    val roll = 7 - new.week
    if (roll > 0) {
        new.add(Calendar.DAY_OF_MONTH, roll)
    }
    return new
}

fun Calendar.monthDiff(that: Calendar): Int {
    val (start, end) = if (this.before(that)) this to that else that to this
    return (end.year - start.year) * 12 + (end.month - start.month)
}