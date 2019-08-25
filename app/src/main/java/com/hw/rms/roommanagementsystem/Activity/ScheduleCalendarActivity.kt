package com.hw.rms.roommanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.applandeo.materialcalendarview.CalendarView
import com.hw.rms.roommanagementsystem.R
import com.imanoweb.calendarview.CustomCalendarView
import java.text.SimpleDateFormat
import java.util.*

class ScheduleCalendarActivity : AppCompatActivity() {

    lateinit var calendar : CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        actionBar?.hide()

        val df = SimpleDateFormat("dd-MMM-yyyy")

        calendar = findViewById(R.id.calendarView)
        calendar.setOnDayClickListener {
            var selectedDates = calendar.selectedDates
            Log.d("date",df.format(it.calendar.time))
        }
    }
}
