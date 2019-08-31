package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
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

    var cal = Calendar.getInstance()

    lateinit var dateSetListener : OnDateSetListener



    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd MMMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_booking)

        initView()
        initButton()
    }

    private fun initView(){

        btnBack = btn_back
        tvBookingDate = tv_booking_date

        dateSetListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                tvBookingDate.text = dateFormat.format(cal.time)

            }



    }

    private fun initButton(){
        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        tvBookingDate.setOnClickListener {
            DatePickerDialog(this,dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }

    }

    override fun onBackPressed() {

    }
}
