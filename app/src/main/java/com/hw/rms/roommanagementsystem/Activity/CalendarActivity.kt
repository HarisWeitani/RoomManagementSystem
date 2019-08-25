package com.hw.rms.roommanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hw.rms.roommanagementsystem.R
import com.imanoweb.calendarview.CustomCalendarView
import java.util.*

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        actionBar?.hide()
//        val calendarView = findViewById<CustomCalendarView>(R.id.calendar_view)
//        calendarView.refreshCalendar(Calendar.getInstance())
    }
}
