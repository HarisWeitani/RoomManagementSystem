package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.hw.rms.roommanagementsystem.R
import java.text.SimpleDateFormat
import java.util.*
import com.hw.rms.roommanagementsystem.Adapter.ScheduleAdapter
import com.hw.rms.roommanagementsystem.Data.DataScheduleByDate
import com.hw.rms.roommanagementsystem.Data.ResponseScheduleByDate
import com.hw.rms.roommanagementsystem.Data.ScheduleData
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.RootActivity
import kotlinx.android.synthetic.main.activity_calendar.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class ScheduleCalendarActivity : AppCompatActivity() {

    //layout header
    lateinit var btnBack : Button
    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView
    lateinit var tv_no_meeting : TextView

    lateinit var calendarView : CalendarView

    var dummyData : MutableList<ScheduleData> = mutableListOf()
    var dummyDataV2 : MutableList<ScheduleData> = mutableListOf()
    var dataEventBySelectedDate : List<DataScheduleByDate?>? = listOf()
    lateinit var scheduleAdapter: ScheduleAdapter

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    lateinit var calendarTitle : TextView
    lateinit var calendarContent : RecyclerView

    var dialog : Dialog? = null
    var apiService : API? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        actionBar?.hide()
        initView()
        try {
            checkIfScreenAlwaysOn()
        }catch (e : Exception){}
    }
    private fun checkIfScreenAlwaysOn(){
        if( DAO.settingsData?.isScreenAlwaysOn!! ) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    private fun initView(){
        btnBack = findViewById(R.id.btnBack)
        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)
        tv_no_meeting = findViewById(R.id.tv_no_meeting)

        calendarTitle = calendar_title
        calendarContent = calendar_content
        apiService = API.networkApi()
        dialog = Dialog(this)

        initDateTime()
        initButtonListener()
        initCalendar()
        initLoadingDialog()
    }

    private fun initDateTime(){
        val date = Date()
        val clockFormat = SimpleDateFormat("HH:mm")
        runOnUiThread{
            tv_date.text = dateFormat.format(date)
            calendarTitle.text = dateFormat.format(date)
            tv_clock.text = clockFormat.format(date)
        }
        Handler().postDelayed({
            initDateTime()
        },10000)
    }

    private fun initButtonListener(){
        btnBack.setOnClickListener {

            finish()
            startActivity(
                Intent(this@ScheduleCalendarActivity, RootActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
    }

    override fun onBackPressed() {

    }

    fun initCalendar() {

        initDummyData()

        dataEventBySelectedDate = DAO.scheduleEventByDate?.data
        scheduleAdapter = ScheduleAdapter(dataEventBySelectedDate as List<DataScheduleByDate>)
        calendar_content.apply {
            layoutManager = LinearLayoutManager(this@ScheduleCalendarActivity)
            adapter = scheduleAdapter
        }

        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDayClickListener {
//            val selectedDates = calendarView.selectedDates
            calendarTitle.text = dateFormat.format(it.calendar.time)

            dialog?.show()
            getEventBySelectedDate(it.calendar.time)

            Log.d("date", dateFormat.format(it.calendar.time))

//            //dummy
//            scheduleAdapter = ScheduleAdapter(dummyDataV2)
//            calendarContent.apply {
//                layoutManager = LinearLayoutManager(this@ScheduleCalendarActivity)
//                adapter = scheduleAdapter
//            }
        }
    }

    private fun getEventBySelectedDate(date : Date){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val currentDate = RequestBody.create(MediaType.parse("text/plain"), dateFormat.format(date))
        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["date"] = currentDate

        apiService!!.getEventByDate(requestBodyMap).enqueue(object :
            Callback<ResponseScheduleByDate> {
            override fun onFailure(call: Call<ResponseScheduleByDate>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@ScheduleCalendarActivity,"Get Event Failed", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseScheduleByDate>?,
                response: Response<ResponseScheduleByDate>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.scheduleEventByDate = response.body()
                    refreshCalendarData()
                    dialog?.dismiss()
                }else{
                    Toast.makeText(this@ScheduleCalendarActivity,"Get Event Failed", Toast.LENGTH_LONG).show()
                }
            }

        })

    }

    fun refreshCalendarData(){
        tv_no_meeting.visibility = View.GONE
        dataEventBySelectedDate = DAO.scheduleEventByDate?.data
        scheduleAdapter = ScheduleAdapter(dataEventBySelectedDate as List<DataScheduleByDate>)
        calendar_content.apply {
            layoutManager = LinearLayoutManager(this@ScheduleCalendarActivity)
            adapter = scheduleAdapter
        }
        if( DAO.scheduleEventByDate?.data?.size!! > 0 ) {
            tv_no_meeting.visibility = View.GONE
        }else{
            tv_no_meeting.visibility = View.VISIBLE
        }
    }

    fun initLoadingDialog(){
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.loading_dialog)
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
