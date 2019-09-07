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
import com.hw.rms.roommanagementsystem.Data.*
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.RootActivity
import okhttp3.MediaType
import okhttp3.RequestBody
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
    lateinit var bottomSchedulePagerAdapter: BottomSchedulePagerAdapter
    var botSchedLeft : MutableList<DataGetNextMeeting> = mutableListOf()
    var botSchedRigt : MutableList<DataGetNextMeeting> = mutableListOf()

    lateinit var bottomSchedulePagerAdapterV2: BottomSchedulePagerAdapterV2
    var botSchedLeftV2 : MutableList<DataUpcomingEvent> = mutableListOf()
    var botSchedRigtV2 : MutableList<DataUpcomingEvent> = mutableListOf()

    lateinit var vpImageVideo: ViewPager

    //image video slideshow
    lateinit var ivAdapter : ImageVideoAdapter
    lateinit var vPager : ViewPager
    var imageVideoList : MutableList<ImageVideo> = mutableListOf()

    lateinit var tv_meeting_title_with_member_name : TextView

    lateinit var btn_check_in : Button
    lateinit var btn_check_out : Button
    lateinit var btnBookNow : Button
    lateinit var btn_schedule : Button

    var booking_status = 0

    var apiService : API? = null
    var loadingDialog : Dialog? = null
    var reviewDialogBuilder : AlertDialog.Builder? = null
    var reviewDialog : AlertDialog? = null
    lateinit var dialogInflater : LayoutInflater

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var instance : MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if( DAO.onMeeting!!.data!!.isNotEmpty() ){
            booking_status =  DAO.onMeeting!!.data!![0]!!.booking_status!!.toInt()
        }

        if( booking_status == 0 ){
            setContentView(R.layout.activity_main_available)
            initAvailableView()
        }else if( booking_status == 1 ){
            setContentView(R.layout.activity_waiting)
            initWaitingView()
        }else if( booking_status == 2 ){
//            if( DAO.nextMeeting?.data?.get(0)?.booking_time_start.isNullOrEmpty() ){
//                setContentView(R.layout.activity_occupied)
//                initOccupiedView()
//            }
            setContentView(R.layout.activity_occupied)
            initOccupiedView()
//            setContentView(R.layout.activity_waiting_occupied)
//            waitingOccupied()
        }
        actionBar?.hide()
        instance = this@MainActivity
        try {
            checkIfScreenAlwaysOn()
        }catch (e : Exception){}

        apiService = API.networkApi()
        loadingDialog = Dialog(this)
        reviewDialogBuilder = AlertDialog.Builder(this)

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

    private fun initWaitingView(){
        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
        tv_meeting_title_with_member_name.text = "${DAO.onMeeting!!.data!![0]!!.meeting_title} by ${DAO.onMeeting!!.data!![0]!!.member_first_name} ${DAO.onMeeting!!.data!![0]!!.member_last_name}"

//        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
        tv_time_meeting_start = findViewById(R.id.tv_time_meeting_start)
        tv_time_meeting_end = findViewById(R.id.tv_time_meeting_end)
        tv_time_meeting_start.text = DAO.onMeeting!!.data!![0]!!.booking_time_start
        tv_time_meeting_end.text = DAO.onMeeting!!.data!![0]!!.booking_time_end

        btn_check_in = findViewById(R.id.btn_check_in)
        btn_check_in.setOnClickListener {
            DAO.onMeeting!!.data!![0]!!.booking_status = "2"
            val i = Intent(this@MainActivity,MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
            overridePendingTransition(0,0)
            startActivity(i)
            overridePendingTransition(0,0)
        }
    }

    private fun waitingOccupied(){

        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
        tv_meeting_title_with_member_name.text = "${DAO.onMeeting!!.data!![0]!!.meeting_title} by ${DAO.onMeeting!!.data!![0]!!.member_first_name} ${DAO.onMeeting!!.data!![0]!!.member_last_name}"

        tv_next_meeting_name = findViewById(R.id.tv_next_meeting_name)
        tv_next_meeting_name.text = "${DAO.onMeeting!!.data!![0]!!.member_first_name} ${DAO.onMeeting!!.data!![0]!!.member_last_name}"

//        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
        tv_time_meeting_start = findViewById(R.id.tv_time_meeting_start)
        tv_time_meeting_end = findViewById(R.id.tv_time_meeting_end)
        tv_time_meeting_start.text = DAO.onMeeting!!.data!![0]!!.booking_time_start
        tv_time_meeting_end.text = DAO.onMeeting!!.data!![0]!!.booking_time_end

        btn_check_in = findViewById(R.id.btn_check_in)
        btn_check_in.setOnClickListener {
            DAO.onMeeting!!.data!![0]!!.booking_status = "2"
            val i = Intent(this@MainActivity,MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
            overridePendingTransition(0,0)
            startActivity(i)
            overridePendingTransition(0,0)
        }

    }

    private fun initOccupiedView(){
        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
        tv_meeting_title_with_member_name.text = "${DAO.onMeeting!!.data!![0]!!.meeting_title} by ${DAO.onMeeting!!.data!![0]!!.member_first_name} ${DAO.onMeeting!!.data!![0]!!.member_last_name}"

        tv_time_meeting_start = findViewById(R.id.tv_time_meeting_start)
        tv_time_meeting_end = findViewById(R.id.tv_time_meeting_end)
        tv_time_meeting_start.text = DAO.onMeeting!!.data!![0]!!.booking_time_start
        tv_time_meeting_end.text = DAO.onMeeting!!.data!![0]!!.booking_time_end

        btn_check_out = findViewById(R.id.btn_check_out)
        btn_check_out.setOnClickListener {
            initReviewDialog()
        }


//        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
//        tv_time_meeting_range.text = "${DAO.onMeeting!!.data!![0]!!.booking_time_start} - ${DAO.onMeeting!!.data!![0]!!.booking_time_end}"
    }

    private fun initNewsViewPager(){
        //news
        for ( i in 0 until DAO.newsFeed!!.data!!.size){
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
        //testing
//        DAO.nextMeeting = Gson().fromJson("{\"ok\":1,\"message\":\"Success Get DataSlideShow\",\"data\":[{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"},{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"},{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"},{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"}]}",
//            ResponseGetNextMeeting::class.java
//        )
        //meeting schedule bottom
        botSchedLeft.clear()
        botSchedRigt.clear()
        val nextMeetingSize = DAO.nextMeeting!!.data!!.size
        if( nextMeetingSize > 0) {
            for (i in 0 until nextMeetingSize) {
                if (i % 2 == 0) {
                    botSchedLeft.add(DAO.nextMeeting!!.data!![i]!!)
                } else {
                    botSchedRigt.add(DAO.nextMeeting!!.data!![i]!!)
                }
            }

            val isRightMeetingNull = DAO.nextMeeting!!.data!!.size % 2 != 0
            if( isRightMeetingNull ) {
                botSchedRigt.add(DataGetNextMeeting())
            }
        }else{
            botSchedLeft.add(DataGetNextMeeting())
            botSchedRigt.add(DataGetNextMeeting())
        }

        bottomSchedulePagerAdapter = BottomSchedulePagerAdapter(botSchedLeft,botSchedRigt,this)
        vpBottomSchedule.adapter = bottomSchedulePagerAdapter

        /***
         * V2 belum dipake
         */
        /*botSchedLeftV2.clear()
        botSchedRigtV2.clear()
        val upcomingEventSize = DAO.upcomingEvent!!.data!!.size
        if( upcomingEventSize > 0) {
            for (i in 0 until upcomingEventSize) {
                if (i % 2 == 0) {
                    botSchedLeftV2.add(DAO.upcomingEvent!!.data!![i]!!)
                } else {
                    botSchedRigtV2.add(DAO.upcomingEvent!!.data!![i]!!)
                }
            }

            val isRightMeetingNull = DAO.upcomingEvent!!.data!!.size % 2 != 0
            if( isRightMeetingNull ) {
                botSchedRigtV2.add(DataUpcomingEvent())
            }
        }else{
            botSchedLeftV2.add(DataUpcomingEvent())
            botSchedRigtV2.add(DataUpcomingEvent())
        }

        bottomSchedulePagerAdapterV2 = BottomSchedulePagerAdapterV2(botSchedLeftV2,botSchedRigtV2,this)
        vpBottomSchedule.adapter = bottomSchedulePagerAdapterV2*/
    }

    private fun initImageVideoPager(){

        val filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath

        val sliderSize = DAO.slideShowData!!.data!!.size

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

//        imageVideoList.add(ImageVideo("tes.jpg",filePath,"",""))
//        imageVideoList.add(ImageVideo("","","pidio.mp4",filePath))
//        imageVideoList.add(ImageVideo("imageview_logo.png",filePath,"",""))
//        imageVideoList.add(ImageVideo("","","tensecvideo.mp4",filePath))

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
        //sample only
        tv_running_text.text = SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT

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
            startActivity(Intent(this@MainActivity,QuickBookingActivity::class.java))
        }

    }

/*    var cal = Calendar.getInstance()
    var time = "17:30:00"
    var dateFormat = SimpleDateFormat("HH:mm:ss")
    var dateFormat2 = SimpleDateFormat("HH:mm")

    var date = dateFormat.parse(time)
    var subs = (date.time - ( 15*60*1000) )
    val now = Date(subs)
    val never = Date()
    dateFormat2.format(now)

    val comp = dateFormat2.format(now).equals("17:15")*/

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
                Toast.makeText(this@MainActivity,"Get Event Failed", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@MainActivity,"Get Event Failed", Toast.LENGTH_SHORT).show()
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
        dialogInflater = layoutInflater
        val dialogView = dialogInflater.inflate(R.layout.review_room_dialog,null)
        reviewDialogBuilder?.setView(dialogView)
        reviewDialogBuilder?.setCancelable(false)

        val iv_review_sad = dialogView.findViewById<ImageView>(R.id.iv_review_sad)
        val iv_review_neutral = dialogView.findViewById<ImageView>(R.id.iv_review_neutral)
        val iv_review_happy = dialogView.findViewById<ImageView>(R.id.iv_review_happy)

        iv_review_sad.setOnClickListener {
            reviewDialog?.dismiss()
            Toast.makeText(this,"Review Sad", Toast.LENGTH_SHORT).show()
        }
        iv_review_neutral.setOnClickListener {
            reviewDialog?.dismiss()
            Toast.makeText(this,"Review Neutral", Toast.LENGTH_SHORT).show()
        }
        iv_review_happy.setOnClickListener {
            reviewDialog?.dismiss()
            Toast.makeText(this,"Review Happy", Toast.LENGTH_SHORT).show()
        }
        reviewDialog = reviewDialogBuilder?.show()
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    /***
     * Get Data By API
     */

    private fun refreshMeetingStatus(){
        getNextMeeting()
        getUpcomingEvents()
        getOnMeeting()
        Handler().postDelayed({
            Log.d("handler_testing", " GET IT ")
            refreshMeetingStatus()
        },60000)
    }

    private fun getNextMeeting(){

        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["room_id"] = body

        apiService!!.getNextMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetNextMeeting>{
            override fun onFailure(call: Call<ResponseGetNextMeeting>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@MainActivity,"get Next Meeting Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseGetNextMeeting>?,
                response: Response<ResponseGetNextMeeting>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.nextMeeting = response.body()
                    initBottomScheduleViewPager()
                }else{

                }
            }
        })
    }

    private fun getOnMeeting(){
        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["room_id"] = body

        apiService!!.getOnMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetOnMeeting>{
            override fun onFailure(call: Call<ResponseGetOnMeeting>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@MainActivity,"get Next Meeting Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseGetOnMeeting>?,
                response: Response<ResponseGetOnMeeting>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){

                    if( DAO.onMeeting != response.body()){
                        DAO.onMeeting = response.body()
                        finish()
                        startActivity(
                            Intent(this@MainActivity, RootActivity::class.java).setFlags(
                                Intent.FLAG_ACTIVITY_SINGLE_TOP))

                    }
                }else{

                }
            }
        })
    }

    private fun getUpcomingEvents(){
        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["location_id"] = body

        apiService!!.googleUpcomingEvent(requestBodyMap).enqueue(object : Callback<ResponseUpcomingEvent>{
            override fun onFailure(call: Call<ResponseUpcomingEvent>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@MainActivity,"get Upcoming Events Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseUpcomingEvent>?,
                response: Response<ResponseUpcomingEvent>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.upcomingEvent = response.body()
                    initBottomScheduleViewPager()
                }else{
                    Toast.makeText(this@MainActivity,"get Upcoming Events Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}
