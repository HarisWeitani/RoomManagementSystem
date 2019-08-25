package com.hw.rms.roommanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.applandeo.materialcalendarview.CalendarView
import com.hw.rms.roommanagementsystem.R
import com.applandeo.materialcalendarview.utils.DateUtils.getCalendar
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener


class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarview = findViewById<CalendarView>(R.id.calendarView)
        calendarview.setOnDayClickListener { eventDay ->
            val clickedDayCalendar = eventDay.calendar
        }

    }
}
