package xyz.dean.androiddemos

import org.junit.Test

import org.junit.Assert.*
import xyz.dean.androiddemos.demos.dcalendar.*
import java.util.Calendar

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_DateUtils() {
        val cal = Calendar.getInstance()
        cal.set(2021, 1 - 1, 4)
        val cal2 = Calendar.getInstance()
        cal2.set(2022, 9 - 1, 1)
        assert(cal.monthDiff(cal2) == cal2.monthDiff(cal))
        println(cal.monthDiff(cal2))
    }
}
