package xyz.dean.androiddemos.demos.dcalendar

import android.os.Bundle
import android.view.View
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import java.util.Calendar
import kotlin.concurrent.thread

class DCalendarActivity : BaseActivity() {
    override fun getDemoItem(): DemoItem = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dcalendar)

        findViewById<View>(R.id.back_view_mask).setOnClickListener { onBackPressed() }
        val calendar = findViewById<DCalendar>(R.id.calendar)
        calendar.setData(
            start = Calendar.getInstance().apply { set(2021, 0, 1) },
            end = Calendar.getInstance().apply { set(2024, 0, 1) },
            asyncDataProvider = { y, m, callback ->
                thread {
                    Thread.sleep(1000)
                    calendar.post { callback("$y-$m") }
                }
            }
        )
        calendar.rollTo(Calendar.getInstance(), false)
    }

    companion object {
        val demoItem = DemoItem("d-calendar",
            R.string.d_calendar_demo_name,
            R.string.d_calendar_describe_text,
            DCalendarActivity::class.java, R.mipmap.img_practice)
    }
}