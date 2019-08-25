package com.hw.rms.roommanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.hw.rms.roommanagementsystem.R
import java.text.SimpleDateFormat
import java.util.*
import com.applandeo.materialcalendarview.EventDay
import com.hw.rms.roommanagementsystem.Adapter.ScheduleAdapter
import com.hw.rms.roommanagementsystem.Data.ScheduleData
import kotlinx.android.synthetic.main.activity_calendar.*


class ScheduleCalendarActivity : AppCompatActivity() {

    //layout header
    lateinit var et_admin_pin : EditText
    lateinit var btnBack : Button
    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView

    lateinit var calendarView : CalendarView

    var dummyData : MutableList<ScheduleData> = mutableListOf()
    var dummyDataV2 : MutableList<ScheduleData> = mutableListOf()
    lateinit var scheduleAdapter: ScheduleAdapter
    lateinit var calendar_RV : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        actionBar?.hide()
        initView()
    }

    private fun initView(){
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
        btnBack.setOnClickListener {
            scheduleAdapter = ScheduleAdapter(dummyDataV2)
            calendar_content.apply {
                layoutManager = LinearLayoutManager(this@ScheduleCalendarActivity)
                adapter = scheduleAdapter
            }
        }
    }

    fun initCalendar() {

        initDummyData()

        scheduleAdapter = ScheduleAdapter(dummyData)

        calendar_content.apply {
            layoutManager = LinearLayoutManager(this@ScheduleCalendarActivity)
            adapter = scheduleAdapter
        }

        val df = SimpleDateFormat("dd-MMM-yyyy")

        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDayClickListener {
            var selectedDates = calendarView.selectedDates
            Log.d("date", df.format(it.calendar.time) + " " + it.calendar.time)
        }
    }

    fun initDummyData(){
        dummyData.add(ScheduleData("13.00 - 14.00","Meeting Dengan Pejabats1"))
        dummyData.add(ScheduleData("14.00 - 15.00","Meeting Dengan Pejabats2"))
        dummyData.add(ScheduleData("16.00 - 17.00","Meeting Dengan Pejabats3"))
        dummyData.add(ScheduleData("18.00 - 19.00","Meeting Dengan Pejabats4"))
        dummyData.add(ScheduleData("20.00 - 21.00","Meeting Dengan Pejabats5"))

        dummyDataV2.add(ScheduleData("08.00 - 09.00","Meeting Dengan Non Pejabat1"))
        dummyDataV2.add(ScheduleData("09.00 - 10.00","Meeting Dengan Non Pejabat2"))
        dummyDataV2.add(ScheduleData("10.00 - 11.00","Meeting Dengan Non Pejabat3"))
        dummyDataV2.add(ScheduleData("11.00 - 12.00","Meeting Dengan Non Pejabat4"))
        dummyDataV2.add(ScheduleData("12.00 - 03.00","Meeting Dengan Non Pejabat5"))

    }

}
