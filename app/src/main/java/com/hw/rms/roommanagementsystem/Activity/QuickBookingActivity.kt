package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hw.rms.roommanagementsystem.R
import com.hw.rms.roommanagementsystem.RootActivity
import kotlinx.android.synthetic.main.activity_admin_setting.*
import kotlinx.android.synthetic.main.activity_quick_booking.*
import java.text.SimpleDateFormat
import java.util.*


class QuickBookingActivity : AppCompatActivity() {

    lateinit var btnBack : Button

    lateinit var tvBookingDate : TextView
    lateinit var tvBookingTimeStart : TextView
    lateinit var etDuration : EditText

    lateinit var btnSubmit : Button

    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView

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

        etDuration = et_duration
        etDuration.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        btnSubmit = btn_submit
        btnSubmit.setOnClickListener {

            showDialog()

            Handler().postDelayed({
                startActivity(Intent(this@QuickBookingActivity,RootActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            },2000)

        }

        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)

        initDateTime()

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

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loading_dialog)

        dialog.show()

    }

    override fun onBackPressed() {

    }
}