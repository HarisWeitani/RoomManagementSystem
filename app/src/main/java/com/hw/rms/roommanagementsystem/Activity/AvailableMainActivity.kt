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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.asura.library.posters.*
import com.asura.library.views.PosterSlider
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Adapter.*
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.hw.rms.roommanagementsystem.Data.DataGetNextMeeting
import com.hw.rms.roommanagementsystem.Data.DataNews
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import com.hw.rms.roommanagementsystem.Model.ImageVideo
import com.hw.rms.roommanagementsystem.Model.News
import com.hw.rms.roommanagementsystem.R
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.hw.rms.roommanagementsystem.Helper.GlobalVal


class AvailableMainActivity : AppCompatActivity(),
    ImageFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentInteractionListener {

    private var SAMPLE_LONG_TEXT: String = "The quick brown fox jumps over the lazy dog "

    lateinit var tv_running_text : TextView
    lateinit var tv_enter_admin : TextView
    lateinit var btn_status : Button

    lateinit var tv_room_name : TextView

    lateinit var iv_logo : ImageView

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

    private fun hideStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()

        if( DAO.onMeeting!!.data!!.isNotEmpty() ){
            booking_status =  DAO.onMeeting!!.data!![0]!!.booking_status!!.toInt()
        }

        if( booking_status == 0 ){
            setContentView(R.layout.activity_main_available)
        }else if( booking_status == 1 ){
            setContentView(R.layout.activity_waiting)
            initWaitingView()
        }else if( booking_status == 2 ){
            setContentView(R.layout.activity_occupied)
            initOccupiedView()
        }

//        setContentView(R.layout.activity_main_available)
//        setContentView(R.layout.activity_occupied)
//        setContentView(R.layout.activity_waiting)
//        setContentView(R.layout.activity_waiting_occupied)

        initView()
        initImageSlider()
        initViewPager()
        initButtonListener()
//        imageVideoAutoScroll()
//        initImageVideoPager()

//        var jsonString = """{"imageName":1,"imagePath":"Test","bijiKuda":"AHSIAPAPPPPPP"}"""
//
//        var gson = Gson()
//        var conv = gson.fromJson(jsonString,ImageVideo::class.java)

    }

    private fun initImageVideoPager(){

        val filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
        imageVideoList.add(ImageVideo("","","",""))

//        ivAdapter = ImageVideoAdapter( supportFragmentManager, filePath, imageVideoList )
//        vPager = findViewById(R.id.view_pager_iv_vv)
//        vPager.adapter = ivAdapter

    }

    private fun initView(){

        //running text below screen
        tv_running_text = findViewById(R.id.tv_running_text)
        tv_running_text.isSelected = true
        //clock
        tv_enter_admin = findViewById(R.id.tv_enter_admin)
        //status
        btn_status = findViewById(R.id.btn_status)
        //poster
        posterSlider = findViewById(R.id.poster_slider)
        //news pager
        vpNews = findViewById(R.id.view_pager_news)
        //iv pager
//        vpImageVideo = findViewById(R.id.view_pager_iv_vv)

        tv_room_name = findViewById(R.id.tv_room_name)
        tv_room_name.text = DAO.settingsData?.room?.room_name

        vpBottomSchedule = findViewById(R.id.view_pager_bottom_schedule)

        iv_logo = findViewById(R.id.iv_logo)
        val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath)

        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile("${imgFile.absolutePath}/${GlobalVal.LOGO_NAME}")
            iv_logo.setImageBitmap(myBitmap)
        }

    }

    private fun initWaitingView(){
        tv_meeting_title_with_member_name = findViewById(R.id.tv_meeting_title_with_member_name)
        tv_meeting_title_with_member_name.text = "${DAO.onMeeting!!.data!![0]!!.meeting_title} by ${DAO.onMeeting!!.data!![0]!!.member_first_name} ${DAO.onMeeting!!.data!![0]!!.member_last_name}"

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
    }

    private fun initViewPager(){

        for ( i in 0 until DAO.newsFeed!!.data!!.size){
            if( i % 2 == 0){
                newsListLeft.add(DAO.newsFeed!!.data!![i]!!)
            }else{
                newsListRight.add(DAO.newsFeed!!.data!![i]!!)
            }
        }

        newsPagerAdapter = NewsPagerAdapter( newsListLeft, newsListRight,this )
        vpNews.adapter = newsPagerAdapter


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

    private fun newsAutoScroll(){
        handlerNews = Handler()
        handlerNews?.postDelayed({
            if( ctrNews < 3 ){
                vpNews.setCurrentItem(ctrNews,true)
                Log.i("ahsiap", " $ctrNews | ${vpNews.currentItem} ")
                ctrNews++
                newsAutoScroll()
            }else{
                ctrNews = 0
                newsAutoScroll()
            }
        }, 5000 )
    }

    private fun imageVideoAutoScroll(){
        imageVideoList.add(ImageVideo("asdasd","asdasd","",""))
        imageVideoList.add(ImageVideo("asdasd","asdasd","",""))

        val absPath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath)
        if( !absPath.exists() ){
            Log.i("ahsiap", "aaaaa")
        }else{
            Log.i("ahsiap","ahsiap")
        }

        val dirPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
        val fileName = "video.mp4"

        imageVideoPagerAdapter = ImageVideoPagerAdapter(imageVideoList,"$dirPath",this)
        vpImageVideo.adapter = imageVideoPagerAdapter
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

    private fun initButtonListener() {
        //sample only
        tv_running_text.text = SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT

        tv_enter_admin.setOnLongClickListener {
            val intent = Intent(this@AvailableMainActivity,
                AdminLoginActivity::class.java)
            startActivity(intent)
            true
        }

    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
