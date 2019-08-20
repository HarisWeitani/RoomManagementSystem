package com.hw.rms.roommanagementsystem.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.asura.library.posters.*
import com.asura.library.views.PosterSlider
import com.hw.rms.roommanagementsystem.Adapter.*
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.hw.rms.roommanagementsystem.Data.DataGetNextMeeting
import com.hw.rms.roommanagementsystem.Data.DataNews
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Model.ImageVideo
import com.hw.rms.roommanagementsystem.R
import java.io.File
import android.graphics.BitmapFactory
import android.widget.*
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Data.ResponseGetNextMeeting
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import kotlinx.android.synthetic.main.activity_main_available.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AvailableMainActivity : AppCompatActivity(),
    ImageFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentInteractionListener {

    private var SAMPLE_LONG_TEXT: String = "The quick brown fox jumps over the lazy dog "

    lateinit var tv_running_text : TextView
    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView
    lateinit var btn_status : Button

    lateinit var tv_room_name : TextView
    lateinit var iv_logo : ImageView
    lateinit var tv_time_meeting_range : TextView

    lateinit var btn_pref_schedule_meeting : Button
    lateinit var btn_next_schedule_meeting : Button

    lateinit var posterSlider: PosterSlider
    var posters : ArrayList<Poster> = arrayListOf()

    //news
    lateinit var vpNews: ViewPager
    lateinit var newsPagerAdapter: NewsPagerAdapter
    var newsListLeft : MutableList<DataNews> = mutableListOf()
    var newsListRight : MutableList<DataNews> = mutableListOf()
    var handlerNews : Handler? = null
    var ctrNews : Int = 0

    //bottom schedule
    lateinit var vpBottomSchedule : ViewPager
    lateinit var bottomSchedulePagerAdapter: BottomSchedulePagerAdapter
    var botSchedLeft : MutableList<DataGetNextMeeting> = mutableListOf()
    var botSchedRigt : MutableList<DataGetNextMeeting> = mutableListOf()


    lateinit var vpImageVideo: ViewPager
    lateinit var imageVideoPagerAdapter: ImageVideoPagerAdapter

    //image video slideshow
    lateinit var ivAdapter : ImageVideoAdapter
    lateinit var vPager : ViewPager
    var imageVideoList : MutableList<ImageVideo> = mutableListOf()

    lateinit var tv_meeting_title_with_member_name : TextView

    lateinit var btn_check_in : Button

    var booking_status = 0

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
            setContentView(R.layout.activity_occupied)
            initOccupiedView()
        }
        actionBar?.hide()


        initView()
//        initImageSlider()
        initViewPager()
        initButtonListener()
        initImageVideoPager()

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
        //poster
//        posterSlider = findViewById(R.id.poster_slider)
        //news pager
        vpNews = findViewById(R.id.view_pager_news)
        //iv pager
        vpImageVideo = findViewById(R.id.view_pager_iv_vv)

        tv_room_name = findViewById(R.id.tv_room_name)
        tv_room_name.text = DAO.settingsData?.room?.room_name

        vpBottomSchedule = findViewById(R.id.view_pager_bottom_schedule)

        iv_logo = findViewById(R.id.iv_logo)
        val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath)

        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile("${imgFile.absolutePath}/${GlobalVal.LOGO_NAME}")
            iv_logo.setImageBitmap(myBitmap)
        }
        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)

        btn_pref_schedule_meeting = findViewById(R.id.btn_pref_schedule_meeting)
        btn_next_schedule_meeting = findViewById(R.id.btn_next_schedule_meeting)

//        val dirPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
//        var video_view = findViewById<VideoView>(R.id.video_view)
//        video_view.setVideoPath("$dirPath/pidio.mp4")
//        var media = MediaController(this)
//        media.setMediaPlayer(video_view)
//        video_view.visibility = View.VISIBLE
//        video_view.start()

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
        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
        tv_time_meeting_range.text = ""
    }

    private fun initWaitingView(){
        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
        tv_meeting_title_with_member_name.text = "${DAO.onMeeting!!.data!![0]!!.meeting_title} by ${DAO.onMeeting!!.data!![0]!!.member_first_name} ${DAO.onMeeting!!.data!![0]!!.member_last_name}"

        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
        tv_time_meeting_range.text = "${DAO.onMeeting!!.data!![0]!!.booking_time_start} - ${DAO.onMeeting!!.data!![0]!!.booking_time_end}"

        btn_check_in = findViewById(R.id.btn_check_in)
        btn_check_in.setOnClickListener {
            DAO.onMeeting!!.data!![0]!!.booking_status = "2"
            val i = Intent(this@AvailableMainActivity,AvailableMainActivity::class.java)
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

        tv_time_meeting_range = findViewById(R.id.tv_time_meeting_range)
        tv_time_meeting_range.text = "${DAO.onMeeting!!.data!![0]!!.booking_time_start} - ${DAO.onMeeting!!.data!![0]!!.booking_time_end}"
    }

    private fun initViewPager(){
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

        //testing
//        DAO.nextMeeting = Gson().fromJson("{\"ok\":1,\"message\":\"Success Get DataSlideShow\",\"data\":[{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"},{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"},{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"},{\"booking_id\":\"369\",\"room_id\":\"23\",\"member_id\":\"1\",\"booking_date\":\"2019-08-19\",\"booking_time_start\":\"23:30:00\",\"booking_time_end\":\"00:00:00\",\"booking_status\":\"1\",\"meeting_status\":\"0\",\"meeting_title\":\"dddddddd23123123\",\"total_participant\":\"5\",\"booking_pin\":\"0\",\"special_request\":\"\",\"additional_package\":\"\",\"created_date\":\"0000-00-00 00:00:00\",\"edited_date\":\"2019-07-30 14:00:04\",\"created_by\":\"1\",\"edited_by\":\"1\",\"member_first_name\":\"Kemendikbud\",\"member_last_name\":\"Group\",\"member_username\":\"test123\",\"member_password\":\"cc03e747a6afbbcbf8be7668acfebee5\",\"member_gender\":\"0\",\"member_email\":\"fandyeffendi24@gmail.com\",\"member_phone\":\"6281617677633\",\"member_address\":\"Kosambi\",\"member_status\":\"1\",\"member_class\":\"0\"}]}",
//            ResponseGetNextMeeting::class.java
//        )
        //meeting schedule bottom
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

    }

    private fun initImageSlider(){

        val path = "android.resource://" + packageName + "/" + R.raw.tiger
        val path2 = "android.resource://" + packageName + "/" + R.raw.tensecvideo

        val dirPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
        val fileName = "video.mp4"

        posters.add(DrawableImage(R.drawable.fox))
        posters.add(RawVideo(R.raw.tensecvideo))
//        posters.add(RemoteVideo(Uri.parse("$dirPath/$fileName")))
        posters.add(RemoteImage(path))

        posterSlider.setPosters(posters)

    }

    private fun initImageVideoPager(){

        val filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

        imageVideoList.add(ImageVideo("tes.jpg",filePath!!,"",""))
        imageVideoList.add(ImageVideo("","","pidio.mp4",filePath))
        imageVideoList.add(ImageVideo("imageview_logo.png",filePath!!,"",""))
        imageVideoList.add(ImageVideo("","","tensecvideo.mp4",filePath))

        ivAdapter = ImageVideoAdapter( supportFragmentManager, filePath, imageVideoList )
        vPager = findViewById(R.id.view_pager_iv_vv)
        vPager.adapter = ivAdapter

    }

    private fun initButtonListener() {
        //sample only
        tv_running_text.text = SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT

        tv_clock.setOnLongClickListener {
            val intent = Intent(this@AvailableMainActivity,
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

    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
