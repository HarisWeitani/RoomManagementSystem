package com.hw.rms.roommanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hw.rms.roommanagementsystem.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import org.threeten.bp.DayOfWeek
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    lateinit var calendar_view : CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        AndroidThreeTen.init(this)

        calendar_view = findViewById(R.id.calendar_view)

        val daysOfWeek = daysOfWeekFromLocale()

        val currentMonth = YearMonth.now()

        calendar_view.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
        calendar_view.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            // Without the kotlin android extensions plugin
             val tv_calendar_day = view.findViewById<TextView>(R.id.tv_calendar_day)
        }


        calendar_view.dayBinder = object : DayBinder<DayViewContainer>{
            override fun create(view: View) =  DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                val calendar_day = container.tv_calendar_day
                calendar_day.text = day.date.dayOfMonth.toString()
            }
        }
    }


    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }
}
