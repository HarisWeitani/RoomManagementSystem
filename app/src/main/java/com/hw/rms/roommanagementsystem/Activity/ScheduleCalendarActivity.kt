package com.hw.rms.roommanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.applandeo.materialcalendarview.CalendarView
import com.hw.rms.roommanagementsystem.R
import java.text.SimpleDateFormat
import java.util.*

class ScheduleCalendarActivity : AppCompatActivity() {

    //layout header
    lateinit var et_admin_pin : EditText
    lateinit var btnBack : Button
    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView

    lateinit var calendar : CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        actionBar?.hide()
        initView()

    }

    private fun initView(){
        //admin pin
        btnBack = findViewById(R.id.btnBack)
        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)
        initDateTime()
        initButtonListener()
        initCalendar()
    }

    private fun initDateTime(){
        val date = Date()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        val clockFormat = SimpleDateFormat("HH:mm")
        runOnUiThread{
            tv_date.text = dateFormat.format(date)
            tv_clock.text = clockFormat.format(date)
        }
        Handler().postDelayed({
            initDateTime()
        },10000)
    }

    private fun initButtonListener(){

    }

    fun initCalendar(){

        val df = SimpleDateFormat("dd-MMM-yyyy")

        calendar = findViewById(R.id.calendarView)
        calendar.setOnDayClickListener {
            var selectedDates = calendar.selectedDates
            Log.d("date",df.format(it.calendar.time))
        }
    }


}
