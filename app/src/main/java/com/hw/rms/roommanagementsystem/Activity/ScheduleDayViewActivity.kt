package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.size
import com.applandeo.materialcalendarview.CalendarView
import com.crashlytics.android.Crashlytics
import com.hw.rms.roommanagementsystem.R
import java.text.SimpleDateFormat
import java.util.*
import com.hw.rms.roommanagementsystem.Adapter.ScheduleAdapter
import com.hw.rms.roommanagementsystem.Data.DataScheduleByDate
import com.hw.rms.roommanagementsystem.Data.ResponseScheduleByDate
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
import kotlin.math.roundToInt


class ScheduleDayViewActivity : AppCompatActivity() {

    //layout header
    lateinit var btnBack : Button
    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView
    lateinit var tv_no_meeting : TextView

    lateinit var calendarView : CalendarView

    var dataEventBySelectedDate : List<DataScheduleByDate?>? = listOf()
    lateinit var scheduleAdapter: ScheduleAdapter

    lateinit var dayViewLayout : RelativeLayout

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val timeFormat = SimpleDateFormat("HH:mm")
    lateinit var calendarTitle : TextView
//    lateinit var calendarContent : RecyclerView
    var eventIndex = 0

    var dialog : Dialog? = null
    var apiService : API? = null

    var prevMeetingTotalFrame : Int = 0
    var isPrevMeetingClash : Boolean = false

    var screenScale : Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_view)
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
        try {
            screenScale = resources.displayMetrics.density
        }catch (e : Exception){Crashlytics.logException(e)}
        dayViewLayout = findViewById(R.id.left_event_column)
        displayDailyEvents()

        calendarTitle = calendar_title
//        calendarContent = calendar_content
        apiService = API.networkApi()
        dialog = Dialog(this)

        initDateTime()
        initButtonListener()
        initCalendar()
        initLoadingDialog()
    }

    private fun displayDailyEvents(){
        var size = 0
        try {
            size = DAO.scheduleEventByDate?.data!!.size
        }catch (e: Exception){
            Crashlytics.logException(e)
        }

        restartEventIndex()

        if( size > 0 ) {
            val sdf = SimpleDateFormat("HH:mm:ss")
            for (x in 0 until size) {
                var startDateTime = DAO.scheduleEventByDate?.data?.get(x)?.start_dateTime
                var endDateTime = DAO.scheduleEventByDate?.data?.get(x)?.end_dateTime
                var eventBlockHeight = getEventTimeFrame(sdf.parse(startDateTime),sdf.parse(endDateTime))
                var meetingMsg = "Meeting"
                try{
                    val summary = DAO.scheduleEventByDate?.data?.get(x)?.summary!!
                    val startTime = DAO.scheduleEventByDate?.data?.get(x)?.start_dateTime!!.substring(0,5)
                    val endTime = DAO.scheduleEventByDate?.data?.get(x)?.end_dateTime!!.substring(0,5)
                    val creator = DAO.scheduleEventByDate?.data?.get(x)?.creator!!
                    meetingMsg = "$summary \n$startTime - $endTime \n$creator"
                }catch (e:Exception){
                    Crashlytics.logException(e)
                    Crashlytics.log("Non Fatal")
                }
                displayEventSection(sdf.parse(startDateTime),eventBlockHeight, meetingMsg)
            }
        }
    }
    private fun restartEventIndex(){
        for( x in 0 until eventIndex ){
            dayViewLayout.removeViewAt((dayViewLayout.size-1))
        }
        eventIndex = 0
        prevMeetingTotalFrame = 0
        isPrevMeetingClash = false
    }
    private fun getEventTimeFrame(start : Date, end : Date): Int{
        var timeDifference = end.time - start.time
        var mCal = Calendar.getInstance()
        mCal.timeInMillis = timeDifference
        var hours = mCal.get(Calendar.HOUR)-7
        var minutes = mCal.get(Calendar.MINUTE)
        return (hours*60) + (minutes)
//        return (hours * 60) + ((minutes * 60) / 100)
    }
    private fun displayEventSection(eventDate : Date, height : Int, message : String){
        val timeFormatter = SimpleDateFormat("HH:mm")
        val displayValue = timeFormatter.format(eventDate)
        val hourMinutes = displayValue.split(":")
        val hours = Integer.parseInt(hourMinutes[0])
        val minutes = Integer.parseInt(hourMinutes[1])
//        val topViewMargin = (hours * 60) + ((minutes * 60) / 100)
        val topViewMargin = (hours * 60) + minutes

        var isMeetingClash = false
        isMeetingClash = prevMeetingTotalFrame >= topViewMargin
        prevMeetingTotalFrame = topViewMargin+height
        createEventView(topViewMargin, height, message, isMeetingClash)
    }
    private fun createEventView(topMargin : Int, height : Int, message : String, isMeetingClash : Boolean){

        var mEventView = TextView(this)
        var lParam = RelativeLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT)
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        lParam.topMargin = ((topMargin * 2) * screenScale).roundToInt()
        if( isMeetingClash && !isPrevMeetingClash){
            isPrevMeetingClash = true
            lParam.leftMargin = (240 * screenScale).roundToInt()
        }else if ( isMeetingClash && isPrevMeetingClash){
            isPrevMeetingClash = false
            lParam.leftMargin = (24 * screenScale).roundToInt()
        }
        else{
            lParam.leftMargin = (24 * screenScale).roundToInt()
        }
        mEventView.layoutParams = lParam
        mEventView.setPadding(12, 0, 12, 0)
        mEventView.height = ((height * 2) * screenScale).roundToInt()
        mEventView.setTextColor(Color.parseColor("#ffffff"))
        mEventView.text = message
        mEventView.background = ContextCompat.getDrawable(this,R.drawable.text_view_box)
        dayViewLayout.addView(mEventView)
        eventIndex++

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
                Intent(this@ScheduleDayViewActivity, RootActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
    }

    override fun onBackPressed() {

    }

    fun initCalendar() {

        dataEventBySelectedDate = DAO.scheduleEventByDate?.data
        scheduleAdapter = ScheduleAdapter(dataEventBySelectedDate as List<DataScheduleByDate>)
//        calendar_content.apply {
//            layoutManager = LinearLayoutManager(this@ScheduleDayViewActivity)
//            adapter = scheduleAdapter
//        }

        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDayClickListener {
            calendarTitle.text = dateFormat.format(it.calendar.time)

            dialog?.show()
            getEventBySelectedDate(it.calendar.time)

            Log.d("date", dateFormat.format(it.calendar.time))
        }
    }

    private fun getEventBySelectedDate(date : Date){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val currentDate = RequestBody.create(MediaType.parse("text/plain"), dateFormat.format(date))
        val location = RequestBody.create(MediaType.parse("text/plain"),  DAO.settingsData!!.room!!.room_code.toString())
        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["date"] = currentDate
        requestBodyMap["location"] = location

        apiService!!.getEventByDate(requestBodyMap).enqueue(object :
            Callback<ResponseScheduleByDate> {
            override fun onFailure(call: Call<ResponseScheduleByDate>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@ScheduleDayViewActivity,"Get Event Failed", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseScheduleByDate>?,
                response: Response<ResponseScheduleByDate>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.scheduleEventByDate = response.body()
//                    refreshCalendarData()
                    displayDailyEvents()
                    dialog?.dismiss()
                }else{
                    Toast.makeText(this@ScheduleDayViewActivity,"Get Event Failed", Toast.LENGTH_LONG).show()
                }
            }

        })

    }

    fun refreshCalendarData(){
        tv_no_meeting.visibility = View.GONE
        dataEventBySelectedDate = DAO.scheduleEventByDate?.data
        scheduleAdapter = ScheduleAdapter(dataEventBySelectedDate as List<DataScheduleByDate>)
//        calendar_content.apply {
//            layoutManager = LinearLayoutManager(this@ScheduleDayViewActivity)
//            adapter = scheduleAdapter
//        }
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

}
