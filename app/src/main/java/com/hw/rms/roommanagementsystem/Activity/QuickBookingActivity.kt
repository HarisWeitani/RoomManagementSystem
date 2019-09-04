package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hw.rms.roommanagementsystem.R
import com.hw.rms.roommanagementsystem.RootActivity
import kotlinx.android.synthetic.main.activity_quick_booking.*
import java.text.SimpleDateFormat
import java.util.*


class QuickBookingActivity : AppCompatActivity() {

    lateinit var btnBack : Button

    lateinit var tvBookingDate : TextView
    lateinit var tvBookingTimeStart : TextView

    lateinit var etDuration : EditText
    lateinit var etMeetingTitle : EditText
    lateinit var etMemberName : EditText
    lateinit var etTotalParticipant : EditText
    lateinit var etRoomFacility : EditText
    lateinit var etFoodSnack : EditText
    lateinit var etAdditionalPackage : EditText
    lateinit var etMeetingStatus : EditText
    lateinit var etSpecialRequest : EditText

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

        etDuration = et_duration
        etMeetingTitle = et_meeting_title
        etMemberName = et_member_name
        etTotalParticipant = et_total_participant
        etRoomFacility = et_room_facility
        etFoodSnack = et_food_snack
        etAdditionalPackage = et_additional_package
        etMeetingStatus = et_meeting_status
        etSpecialRequest = et_special_request

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

        btnSubmit = btn_submit
        btnSubmit.setOnClickListener {

            submitQuickBooking()

//            Handler().postDelayed({
//                startActivity(Intent(this@QuickBookingActivity,RootActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
//            },2000)

        }

        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)

        initDateTime()

    }

    private fun submitQuickBooking(){
        showLoadingDialog()
    }

    private fun validateBookingData(){

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

    fun showLoadingDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loading_dialog)

        dialog.show()

    }

    override fun onBackPressed() {

    }
}
