package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hw.rms.roommanagementsystem.R
import kotlinx.android.synthetic.main.activity_quick_booking.*
import java.text.SimpleDateFormat
import java.util.*


class QuickBookingActivity : AppCompatActivity() {

    lateinit var btnBack : Button

    lateinit var tvBookingDate : TextView
    lateinit var tvBookingTimeStart : TextView

    var cal = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd MMMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_booking)

        initView()
    }

    private fun initView(){

        btnBack = btn_back

        btnBack.setOnClickListener {
            super.onBackPressed()
        }


        tvBookingDate = tv_booking_date

        tvBookingDate.setOnClickListener {
            DatePickerDialog(this,OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                tvBookingDate.setTextColor(Color.WHITE)
                tvBookingDate.text = dateFormat.format(cal.time)

            },  cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        tvBookingTimeStart = tv_booking_time_start
        tvBookingTimeStart.setOnClickListener {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(this,TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                tvBookingTimeStart.setTextColor(Color.WHITE)
                val hour: String
                val min: String

                if( hourOfDay < 10) hour = "0$hourOfDay"
                else hour = "$hourOfDay"
                if( minute < 10 ) min = "0$minute"
                else min = "$minute"

                tvBookingTimeStart.text = "$hour : $min"

            },hour,minute,true).show()
        }




    }

    override fun onBackPressed() {

    }
}
