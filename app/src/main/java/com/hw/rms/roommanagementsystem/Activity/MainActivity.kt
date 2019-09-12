package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.hw.rms.roommanagementsystem.Adapter.*
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.Model.ImageVideo
import com.hw.rms.roommanagementsystem.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.hw.rms.roommanagementsystem.Data.*
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.RootActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class MainActivity : AppCompatActivity(),
    ImageFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentInteractionListener {

    private var SAMPLE_LONG_TEXT: String = "The quick brown fox jumps over the lazy dog "

    lateinit var tv_running_text : TextView
    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView
    lateinit var btn_status : Button

    lateinit var tv_room_name : TextView
    lateinit var iv_logo : ImageView
//    lateinit var tv_time_meeting_range : TextView
    lateinit var tv_time_meeting_start : TextView
    lateinit var tv_time_meeting_end : TextView

    lateinit var btn_pref_schedule_meeting : Button
    lateinit var btn_next_schedule_meeting : Button

    lateinit var tv_next_meeting_name : TextView

    //news
    lateinit var vpNews: ViewPager
    lateinit var newsPagerAdapter: NewsPagerAdapter
    var newsListLeft : MutableList<DataNews> = mutableListOf()
    var newsListRight : MutableList<DataNews> = mutableListOf()

    //bottom schedule
    lateinit var vpBottomSchedule : ViewPager
/*    lateinit var bottomSchedulePagerAdapter: BottomSchedulePagerAdapter
    var botSchedLeft : MutableList<DataGetNextMeeting> = mutableListOf()
    var botSchedRigt : MutableList<DataGetNextMeeting> = mutableListOf()*/

    lateinit var bottomSchedulePagerAdapterV2: BottomSchedulePagerAdapterV2
    var botSchedLeftV2 : MutableList<DataGetNextMeeting> = mutableListOf()
    var botSchedRigtV2 : MutableList<DataGetNextMeeting> = mutableListOf()

    lateinit var vpImageVideo: ViewPager

    //image video slideshow
    lateinit var ivAdapter : ImageVideoAdapter
    lateinit var vPager : ViewPager
    var imageVideoList : MutableList<ImageVideo> = mutableListOf()

    lateinit var tv_meeting_title_with_member_name : TextView

    lateinit var btnBookNow : Button
    lateinit var btn_schedule : Button
    lateinit var btn_extend : Button

    var booking_status = "available"

    var apiService : API? = null
    var isGetNextMeeting: Boolean = false
    var isGetCurrentMeeting: Boolean = false
    var loadingDialog : Dialog? = null

    var reviewDialogBuilder : AlertDialog.Builder? = null
    var reviewDialog : AlertDialog? = null
    lateinit var reviewDialogInflater : LayoutInflater

    var extendDialogBuilder : AlertDialog.Builder? = null
    var extendDialog : AlertDialog? = null
    lateinit var extendDialogInflater : LayoutInflater

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var instance : MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if( DAO.currentMeeting?.ok == 1 ){
//            booking_status =  DAO.currentMeeting!!.data!!.booking_status!!.toInt()
            booking_status =  DAO.currentMeeting!!.data!!.status!!
        }
        if( booking_status == "available" ){
            setContentView(R.layout.activity_main_available)
            initAvailableView()
        }
//        else if( booking_status == 1 ){
//            setContentView(R.layout.activity_waiting)
////            initWaitingView()
//        }
        else if( booking_status == "confirmed" ){
//            setContentView(R.layout.activity_occupied)
            setContentView(R.layout.activity_occupied_v2)
            initOccupiedView()
        }
        actionBar?.hide()
        instance = this@MainActivity
        try {
            checkIfScreenAlwaysOn()
        }catch (e : Exception){}

        apiService = API.networkApi()
        loadingDialog = Dialog(this)
        reviewDialogBuilder = AlertDialog.Builder(this)
        extendDialogBuilder = AlertDialog.Builder(this)

        initView()
        initNewsViewPager()
        initBottomScheduleViewPager()
        initButtonListener()
        initImageVideoPager()
        initLoadingDialog()
        refreshMeetingStatus()
    }

    private fun checkIfScreenAlwaysOn(){
        if( DAO.settingsData?.isScreenAlwaysOn!! ) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initView(){

        //running text below screen
        tv_running_text = findViewById(R.id.tv_running_text)
        tv_running_text.isSelected = true
        //clock
        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)
        initDateTime()
        //status
        btn_status = findViewById(R.id.btn_status)
        //news pager
        vpNews = findViewById(R.id.view_pager_news)
        //iv pager
        vpImageVideo = findViewById(R.id.view_pager_iv_vv)

        tv_room_name = findViewById(R.id.tv_room_name)
        tv_room_name.text = DAO.settingsData?.room?.room_name

        vpBottomSchedule = findViewById(R.id.view_pager_bottom_schedule)

        btnBookNow = findViewById(R.id.btn_book_now)

        iv_logo = findViewById(R.id.iv_logo)
        val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath)

        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile("${imgFile.absolutePath}/${GlobalVal.LOGO_NAME}")
            iv_logo.setImageBitmap(myBitmap)
        }
        tv_time_meeting_start = findViewById(R.id.tv_time_meeting_start)
        tv_time_meeting_end = findViewById(R.id.tv_time_meeting_end)

        btn_pref_schedule_meeting = findViewById(R.id.btn_pref_schedule_meeting)
        btn_next_schedule_meeting = findViewById(R.id.btn_next_schedule_meeting)

        btn_schedule = findViewById(R.id.btn_schedule)

    }

    override fun onPause() {
        super.onPause()
        Log.d("ASD","asdasdasd")
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

    private fun initAvailableView(){
//        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
//        tv_time_meeting_range.text = ""

    }

    /*private fun initWaitingView(){
        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
//        tv_meeting_title_with_member_name.text = "${DAO.currentMeeting!!.data!![0]!!.meeting_title} by ${DAO.currentMeeting!!.data!![0]!!.member_first_name} ${DAO.currentMeeting!!.data!![0]!!.member_last_name}"

//        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
        tv_time_meeting_start = findViewById(R.id.tv_time_meeting_start)
        tv_time_meeting_end = findViewById(R.id.tv_time_meeting_end)
        tv_time_meeting_start.text = DAO.currentMeeting!!.data!!.start_dateTime
        tv_time_meeting_end.text = DAO.currentMeeting!!.data!!.end_dateTime

        btn_check_in = findViewById(R.id.btn_check_in)
        btn_check_in.setOnClickListener {
            val i = Intent(this@MainActivity,MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
            overridePendingTransition(0,0)
            startActivity(i)
            overridePendingTransition(0,0)
        }
    }*/

    /*private fun waitingOccupied(){

        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
        tv_meeting_title_with_member_name.text = "${DAO.currentMeeting!!.data!![0]!!.meeting_title} by ${DAO.currentMeeting!!.data!![0]!!.member_first_name} ${DAO.currentMeeting!!.data!![0]!!.member_last_name}"

        tv_next_meeting_name = findViewById(R.id.tv_next_meeting_name)
        tv_next_meeting_name.text = "${DAO.currentMeeting!!.data!![0]!!.member_first_name} ${DAO.currentMeeting!!.data!![0]!!.member_last_name}"

//        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
        tv_time_meeting_start = findViewById(R.id.tv_time_meeting_start)
        tv_time_meeting_end = findViewById(R.id.tv_time_meeting_end)
        tv_time_meeting_start.text = DAO.currentMeeting!!.data!![0]!!.booking_time_start
        tv_time_meeting_end.text = DAO.currentMeeting!!.data!![0]!!.booking_time_end

        btn_check_in = findViewById(R.id.btn_check_in)
        btn_check_in.setOnClickListener {
            DAO.currentMeeting!!.data!![0]!!.booking_status = "2"
            val i = Intent(this@MainActivity,MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
            overridePendingTransition(0,0)
            startActivity(i)
            overridePendingTransition(0,0)
        }

    }*/

    private fun initOccupiedView(){
        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
//        tv_meeting_title_with_member_name.text = "${DAO.currentMeeting!!.data!![0]!!.meeting_title} by ${DAO.currentMeeting!!.data!![0]!!.member_first_name} ${DAO.currentMeeting!!.data!![0]!!.member_last_name}"
        tv_meeting_title_with_member_name.text = "${DAO.currentMeeting?.data?.summary}"

        tv_time_meeting_start = findViewById(R.id.tv_time_meeting_start)
        tv_time_meeting_end = findViewById(R.id.tv_time_meeting_end)
        tv_time_meeting_start.text = DAO.currentMeeting!!.data!!.start_dateTime
        tv_time_meeting_end.text = DAO.currentMeeting!!.data!!.end_dateTime

        btn_extend = findViewById(R.id.btn_extend)
        btn_extend.setOnClickListener {
            //show dialog
            initExtendDialog()
//            initReviewDialog()
//            extendCurrentMeeting()
        }

//        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
//        tv_time_meeting_range.text = "${DAO.currentMeeting!!.data!![0]!!.booking_time_start} - ${DAO.currentMeeting!!.data!![0]!!.booking_time_end}"
    }

    private fun extendCurrentMeeting( newTime : Int ){
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val timeFormat = SimpleDateFormat("HH:mm")
        val timeEndOld = DAO.currentMeeting?.data?.end_dateTime
        val timeConv = timeFormat.parse(timeEndOld)
        val subs = ( timeConv.time + (15*60*1000) )
        val timeEndNew = timeFormat.format(subs)+":00"

        var id = RequestBody.create(MediaType.parse("text/plain"), DAO.currentMeeting?.data?.id)
        var newEndDate = RequestBody.create(MediaType.parse("text/plain"), dateFormat.format(date) )
        var newEndTime = RequestBody.create(MediaType.parse("text/plain"), timeEndNew)

        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["id"] = id
        requestBodyMap["new_end_date"] = newEndDate
        requestBodyMap["new_end_time"] = newEndTime

        apiService!!.googleExtendEvent(requestBodyMap).enqueue(object : Callback<ResponseExtendEvent>{
            override fun onFailure(call: Call<ResponseExtendEvent>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@MainActivity,"Extend Time Failed", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseExtendEvent>?,
                response: Response<ResponseExtendEvent>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    loadingDialog?.dismiss()
                    finish()
                    startActivity(
                        Intent(this@MainActivity, RootActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_SINGLE_TOP))
                }else{
                    Toast.makeText(this@MainActivity,"Extend Time Failed", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun checkIfSurveyTimeToShow(){
        var time = DAO.currentMeeting?.data?.end_dateTime
        var dateFormat2 = SimpleDateFormat("HH:mm")

        var date = dateFormat2.parse(time)

        var timeIterval = 15
        try{
            timeIterval = DAO.configData?.config_timestamp!!.toInt()
        }catch (e : Exception){
            Crashlytics.logException(e)
        }
        var subs = (date.time - ( timeIterval*60*1000) )
        val dateParam = Date(subs)

        val showTime = dateFormat2.format(dateParam)

        if( dateFormat2.format(Date()).equals(showTime) ){
            initReviewDialog()
        }
    }

    private fun initNewsViewPager(){
        var newsSize = 0
        try{
            newsSize = DAO.newsFeed?.data!!.size
        }catch (e : Exception){
            Crashlytics.logException(e)
        }
        for ( i in 0 until newsSize){
            if( i % 2 == 0){
                newsListLeft.add(DAO.newsFeed!!.data!![i]!!)
            }else{
                newsListRight.add(DAO.newsFeed!!.data!![i]!!)
            }
        }

        newsPagerAdapter = NewsPagerAdapter( newsListLeft, newsListRight,this )
        vpNews.adapter = newsPagerAdapter
    }

    private fun initBottomScheduleViewPager(){
        botSchedLeftV2.clear()
        botSchedRigtV2.clear()
        var upcomingEventSize = 0
        try {
            upcomingEventSize = DAO.nextMeeting!!.data!!.size
        }catch (e : Exception ){
            upcomingEventSize = 0
        }
        if( upcomingEventSize > 0) {
            for (i in 0 until upcomingEventSize) {
                if (i % 2 == 0) {
                    botSchedLeftV2.add(DAO.nextMeeting!!.data!![i]!!)
                } else {
                    botSchedRigtV2.add(DAO.nextMeeting!!.data!![i]!!)
                }
            }

            val isRightMeetingNull = DAO.nextMeeting!!.data!!.size % 2 != 0
            if( isRightMeetingNull ) {
                botSchedRigtV2.add(DataGetNextMeeting())
            }
        }else{
            botSchedLeftV2.add(DataGetNextMeeting())
            botSchedRigtV2.add(DataGetNextMeeting())
        }

        bottomSchedulePagerAdapterV2 = BottomSchedulePagerAdapterV2(botSchedLeftV2,botSchedRigtV2,this)
        vpBottomSchedule.adapter = bottomSchedulePagerAdapterV2
    }

    private fun initImageVideoPager(){

        val filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        var sliderSize = 0
        try{
            sliderSize = DAO.slideShowData?.data!!.size
        }catch (e : Exception){
            Crashlytics.logException(e)
        }
        //testing purpose
        sliderSize = 1
        for ( x in 0 until sliderSize ){
            val dataTemp = DAO.slideShowData!!.data!![x]
            if( dataTemp!!.slideshow_type.equals("1") ){
                val filename = "${dataTemp.slideshow_id}${dataTemp.slideshow_name}.png"
                if(checkIfFileExist(filename)) imageVideoList.add(ImageVideo(filename,filePath,"",""))
            }
            else if (dataTemp.slideshow_type.equals("2") ){
                val filename = "${dataTemp.slideshow_id}${dataTemp.slideshow_name}.mp4"
                if(checkIfFileExist(filename))imageVideoList.add(ImageVideo("","",filename,filePath))
            }
        }

        ivAdapter = ImageVideoAdapter( supportFragmentManager, filePath, imageVideoList )
        vPager = findViewById(R.id.view_pager_iv_vv)
        vPager.adapter = ivAdapter

    }

    private fun checkIfFileExist( file : String ) : Boolean{
        val filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        val f = File("$filePath/$file")
        return f.exists()
    }

    fun setNextImageVideoPager(){
        if ( vPager.currentItem  < imageVideoList.size-1 ) {
            vPager.currentItem++
        }else{
            vPager.currentItem = 0
        }
    }

    private fun initButtonListener() {

        var runText = ""
        try {
            for (x in 0 until DAO.runningText!!.size) {
                runText += DAO.runningText!![x].running_text
            }
            tv_running_text.text = runText
        }catch (e : Exception){
            tv_running_text.text = ""
        }

        tv_clock.setOnLongClickListener {
            val intent = Intent(this@MainActivity,
                AdminLoginActivity::class.java)
            startActivity(intent)
            true
        }

        btn_pref_schedule_meeting.setOnClickListener {
            if( vpBottomSchedule.currentItem > 0 ){
                vpBottomSchedule.currentItem --
            }
        }

        btn_next_schedule_meeting.setOnClickListener {
            if( vpBottomSchedule.currentItem < vpBottomSchedule.childCount ){
                vpBottomSchedule.currentItem ++
            }
        }

        btn_schedule.setOnClickListener {
            getEventByDateNow()
            loadingDialog?.show()
        }

        btnBookNow.setOnClickListener {
            finish()
            startActivity(Intent(this@MainActivity,QuickBookingActivity::class.java))
        }

    }

    private fun getEventByDateNow(){
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val currentDate = RequestBody.create(MediaType.parse("text/plain"), dateFormat.format(date))
        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["date"] = currentDate

        apiService!!.getEventByDate(requestBodyMap).enqueue(object : Callback<ResponseScheduleByDate>{
            override fun onFailure(call: Call<ResponseScheduleByDate>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                loadingDialog?.dismiss()
                Toast.makeText(this@MainActivity,"Get Event Failed", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseScheduleByDate>?,
                response: Response<ResponseScheduleByDate>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.scheduleEventByDate = response.body()
                    startActivity(Intent(this@MainActivity,ScheduleCalendarActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    loadingDialog?.dismiss()
                }else{
                    loadingDialog?.dismiss()
                    Toast.makeText(this@MainActivity,"Get Event Failed", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    fun initLoadingDialog(){
        loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog?.setCancelable(false)
        loadingDialog?.setContentView(R.layout.loading_dialog)
    }

    fun initReviewDialog(){
        reviewDialogInflater = layoutInflater
        val dialogView = reviewDialogInflater.inflate(R.layout.review_room_dialog,null)
        reviewDialogBuilder?.setView(dialogView)
        reviewDialogBuilder?.setCancelable(false)

        val iv_review_sad = dialogView.findViewById<ImageView>(R.id.iv_review_sad)
        val iv_review_neutral = dialogView.findViewById<ImageView>(R.id.iv_review_neutral)
        val iv_review_happy = dialogView.findViewById<ImageView>(R.id.iv_review_happy)

        iv_review_sad.setOnClickListener {
            reviewDialog?.dismiss()
            sendSurvey("BAD")
            Toast.makeText(this,"Review Sad", Toast.LENGTH_SHORT).show()
        }
        iv_review_neutral.setOnClickListener {
            reviewDialog?.dismiss()
            sendSurvey("NEUTRAL")
            Toast.makeText(this,"Review Neutral", Toast.LENGTH_SHORT).show()
        }
        iv_review_happy.setOnClickListener {
            reviewDialog?.dismiss()
            sendSurvey("GOOD")
            Toast.makeText(this,"Review Happy", Toast.LENGTH_SHORT).show()
        }
        reviewDialog = reviewDialogBuilder?.show()
    }

    private fun sendSurvey(status : String){
        var id = RequestBody.create(MediaType.parse("text/plain"), DAO.currentMeeting?.data?.id)
        val location = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_code )
        val summary = RequestBody.create(MediaType.parse("text/plain"), DAO.currentMeeting?.data?.summary)
        val start_date = RequestBody.create(MediaType.parse("text/plain"), DAO.currentMeeting?.data?.start_dateTime)
        val end_date = RequestBody.create(MediaType.parse("text/plain"), DAO.currentMeeting?.data?.end_dateTime)
        val result = RequestBody.create(MediaType.parse("text/plain"), status)

        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["id"] = id
        requestBodyMap["summary"] = summary
        requestBodyMap["location"] = location
        requestBodyMap["start_dateTime"] = start_date
        requestBodyMap["end_dateTime"] = end_date
        requestBodyMap["survey_result"] = result

        apiService!!.addSurvey(requestBodyMap).enqueue(object : Callback<ResponseSurvey>{
            override fun onFailure(call: Call<ResponseSurvey>?, t: Throwable?) {
                Toast.makeText(this@MainActivity,"Send Survey Failed", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseSurvey>?,
                response: Response<ResponseSurvey>?
            ) {
                Toast.makeText(this@MainActivity,"Send Survey Success", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun initExtendDialog(){
        val extend_interval = DAO.configData?.config_timestamp
        var extendTime = 0

        extendDialogInflater = layoutInflater
        val dialogView = extendDialogInflater.inflate(R.layout.extend_room_dialog,null)
        extendDialogBuilder?.setView(dialogView)
        extendDialogBuilder?.setCancelable(true)

        val iv_minus_time = dialogView.findViewById<ImageView>(R.id.iv_minus_time)
        val tv_time_extend = dialogView.findViewById<TextView>(R.id.tv_time_extend)
        val iv_plus_time = dialogView.findViewById<ImageView>(R.id.iv_plus_time)
        val btn_confirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        tv_time_extend.text = "$extendTime"

        iv_minus_time.setOnClickListener {
            try {
                extendTime -= extend_interval!!.toInt()
                if( extendTime < 0 ) {
                    extendTime = 0
                    runOnUiThread {
                        tv_time_extend.text = "$extendTime"
                    }
                }else{
                    runOnUiThread {
                        tv_time_extend.text = "$extendTime"
                    }
                }
            }catch (e: Exception){
                Toast.makeText(this@MainActivity,"Add Time Failed", Toast.LENGTH_LONG).show()
            }
        }
        iv_plus_time.setOnClickListener {
            try {
                extendTime += extend_interval!!.toInt()
                runOnUiThread {
                    tv_time_extend.text = "$extendTime"
                }
            }catch (e: Exception){
                Toast.makeText(this@MainActivity,"Add Time Failed", Toast.LENGTH_LONG).show()
            }
        }
        btn_confirm.setOnClickListener {
            if(extendTime <= 0 ) {
                extendDialog?.dismiss()
            }else{
                extendDialog?.dismiss()
                loadingDialog?.show()
                extendCurrentMeeting(extendTime)
            }
        }

        extendDialog = extendDialogBuilder?.show()
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    /***
     * Get Data By API
     */

    private fun refreshMeetingStatus(){
        Handler().postDelayed({
            Log.d("handler_testing", " GET IT ")
            getNextMeeting()
            getCurrentMeeting()
            if( isGetCurrentMeeting ) checkIfSurveyTimeToShow()
            refreshMeetingStatus()
        },60000)
    }

    private fun getNextMeeting(){

        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["room_id"] = body

        apiService!!.getNextMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetNextMeeting>{
            override fun onFailure(call: Call<ResponseGetNextMeeting>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getNextMeeting",t.toString())
                Toast.makeText(this@MainActivity,"get Next Meeting Failed", Toast.LENGTH_LONG).show()
                isGetNextMeeting = false
            }

            override fun onResponse(
                call: Call<ResponseGetNextMeeting>?,
                response: Response<ResponseGetNextMeeting>?
            ) {
                GlobalVal.networkLogging("onResponse getNextMeeting",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.nextMeeting = response.body()
                    isGetNextMeeting = DAO.nextMeeting != null
                }else{
                    isGetNextMeeting = false
                    Toast.makeText(this@MainActivity,"get Next Meeting Failed", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun getCurrentMeeting(){
        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["room_id"] = body

        apiService!!.getCurrentMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetCurrentMeeting>{
            override fun onFailure(call: Call<ResponseGetCurrentMeeting>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getCurrentMeeting",t.toString())
                Toast.makeText(this@MainActivity,"get On Meeting Failed", Toast.LENGTH_LONG).show()
                isGetCurrentMeeting = false
            }
            override fun onResponse(
                call: Call<ResponseGetCurrentMeeting>?,
                response: Response<ResponseGetCurrentMeeting>?
            ) {
                GlobalVal.networkLogging("onResponse getCurrentMeeting",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    isGetCurrentMeeting = response.body().data != null
                    if( DAO.currentMeeting != response.body()){
                        DAO.currentMeeting = response.body()
                        finish()
                        startActivity(
                            Intent(this@MainActivity, RootActivity::class.java).setFlags(
                                Intent.FLAG_ACTIVITY_SINGLE_TOP))

                    }
                }else{
                    isGetCurrentMeeting = false
                    Toast.makeText(this@MainActivity,"get On Meeting Failed", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
