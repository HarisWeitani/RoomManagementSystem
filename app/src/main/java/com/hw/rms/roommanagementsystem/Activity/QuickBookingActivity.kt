package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.hw.rms.roommanagementsystem.Data.ResponseAddEvent
import com.hw.rms.roommanagementsystem.Data.ResponseGetOnMeeting
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.R
import kotlinx.android.synthetic.main.activity_quick_booking_old.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class QuickBookingActivity : AppCompatActivity() {

    lateinit var btnBack : Button

/*    lateinit var tvBookingDate : TextView
    lateinit var tvBookingTimeStart : TextView

    lateinit var etDuration : EditText
    lateinit var etMeetingTitle : EditText
    lateinit var etMemberName : EditText
    lateinit var etTotalParticipant : EditText
    lateinit var etRoomFacility : EditText
    lateinit var etFoodSnack : EditText
    lateinit var etAdditionalPackage : EditText
    lateinit var etMeetingStatus : EditText
    lateinit var etSpecialRequest : EditText*/

    lateinit var et_location : EditText
    lateinit var et_summary : EditText
    lateinit var tv_booking_date : TextView
    lateinit var tv_booking_time_start : TextView
    lateinit var tv_booking_time_end : TextView
    lateinit var et_description : EditText
    lateinit var et_attendees_email : EditText
    lateinit var add_more_attendees : ImageView
    lateinit var btnSubmit : Button

    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView

    lateinit var layout_attendees_email : LinearLayout
    var attendeesEmailETList : MutableList<EditText>? = mutableListOf()
    var emailList : MutableList<String>? = mutableListOf()
    var etIdDynamic = 0

    var cal = Calendar.getInstance()

    var dialog : Dialog? = null
    var apiService : API? = null

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd MMMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_booking)

        initView()
        apiService = API.networkApi()
        dialog = Dialog(this)
        initLoadingDialog()
    }

    private fun initView(){

        layout_attendees_email = findViewById(R.id.layout_attendees_email)

        et_location = findViewById(R.id.et_location)
        et_summary = findViewById(R.id.et_summary)

        tv_booking_date = findViewById(R.id.tv_booking_date)
        tv_booking_date.setOnClickListener {
            DatePickerDialog(this,OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                tv_booking_date.setTextColor(Color.WHITE)
                tv_booking_date.text = dateFormat.format(cal.time)

            },  cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        tv_booking_time_start = findViewById(R.id.tv_booking_time_start)
        tv_booking_time_start.setOnClickListener {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(this,TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                tv_booking_time_start.setTextColor(Color.WHITE)
                val hour: String
                val min: String

                if( hourOfDay < 10) hour = "0$hourOfDay"
                else hour = "$hourOfDay"
                if( minute < 10 ) min = "0$minute"
                else min = "$minute"

                tv_booking_time_start.text = "$hour : $min"

            },hour,minute,true).show()
        }

        tv_booking_time_end = findViewById(R.id.tv_booking_time_end)
        tv_booking_time_end.setOnClickListener {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(this,TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                tv_booking_time_end.setTextColor(Color.WHITE)
                val hour: String
                val min: String

                if( hourOfDay < 10) hour = "0$hourOfDay"
                else hour = "$hourOfDay"
                if( minute < 10 ) min = "0$minute"
                else min = "$minute"

                tv_booking_time_end.text = "$hour : $min"

            },hour,minute,true).show()
        }

        add_more_attendees = findViewById(R.id.add_more_attendees)
        add_more_attendees.setOnClickListener {
            addAttendeesLine()
        }

        et_description = findViewById(R.id.et_description)
        et_attendees_email = findViewById(R.id.et_attendees_email)

        btnSubmit = findViewById(R.id.btn_submit)
        btnSubmit.setOnClickListener {
            submitQuickBooking()
        }

        btnBack = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)
        initDateTime()
    }

    private fun addAttendeesLine(){
        var et = EditText(this)
        var p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        p.setMargins(0,0,0,16)
        et.layoutParams = p
        et.hint = "Attendees Email"
        et.id = etIdDynamic
        et.background = ContextCompat.getDrawable(this,R.drawable.edit_text_box)

        attendeesEmailETList?.add(et)
        layout_attendees_email.addView(et)
        etIdDynamic++
    }

    private fun getAllAttendees(){
        for (x in 0 until attendeesEmailETList!!.size){
            if(attendeesEmailETList!![x].text.toString().isNotEmpty()){
                emailList?.add(attendeesEmailETList!![x].text.toString())
            }
        }
    }

    private fun submitQuickBooking(){
        if( validateBookingData() ){
            getAllAttendees()
            submitData()
        }else{
            Toast.makeText(this@QuickBookingActivity,"All Fields Must Be Filled", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateBookingData() : Boolean{
        var isValidToSubmit = false
        isValidToSubmit = et_location.text.isNotEmpty()
        isValidToSubmit = et_summary.text.isNotEmpty()
        isValidToSubmit = !tv_booking_date.text.equals("Booking Date")
        isValidToSubmit = !tv_booking_time_start.text.equals("Booking Start")
        isValidToSubmit = !tv_booking_time_end.text.equals("Booking End")
        isValidToSubmit = et_description.text.isNotEmpty()
        isValidToSubmit = et_attendees_email.text.isNotEmpty()

        return isValidToSubmit
    }

    private fun submitData(){
        var location = RequestBody.create(MediaType.parse("text/plain"), et_location.text.toString())
        var summary = RequestBody.create(MediaType.parse("text/plain"), et_summary.text.toString())
        var description = RequestBody.create(MediaType.parse("text/plain"), et_description.text.toString())
        var start_date = RequestBody.create(MediaType.parse("text/plain"), tv_booking_date.text.toString())
        var end_date = RequestBody.create(MediaType.parse("text/plain"), tv_booking_date.text.toString())
        var start_time = RequestBody.create(MediaType.parse("text/plain"), tv_booking_time_start.text.toString())
        var end_time = RequestBody.create(MediaType.parse("text/plain"), tv_booking_time_end.text.toString())
        var attendees_email = RequestBody.create(MediaType.parse("text/plain"), et_attendees_email.text.toString())

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["location"] = location
        requestBodyMap["summary"] = summary
        requestBodyMap["description"] = description
        requestBodyMap["start_date"] = start_date
        requestBodyMap["end_date"] = end_date
        requestBodyMap["start_time"] = start_time
        requestBodyMap["end_time"] = end_time
        requestBodyMap["attendees_email[]"] = attendees_email
//        for( x in 0 until emailList!!.size ){
//            var attendees_emails = RequestBody.create(MediaType.parse("text/plain"), emailList!![0])
//            requestBodyMap["attendees_email[]"] = attendees_emails
//        }

        apiService!!.googleAddEvent(requestBodyMap).enqueue(object : Callback<ResponseAddEvent> {
            override fun onFailure(call: Call<ResponseAddEvent>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@QuickBookingActivity,"get Next Meeting Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseAddEvent>?,
                response: Response<ResponseAddEvent>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){

                }else{

                }
            }
        })
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

    fun initLoadingDialog(){
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.loading_dialog)
    }

    override fun onBackPressed() {

    }

    /* Using quick Booking Old
    private fun initViewOld(){

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
*/
}
