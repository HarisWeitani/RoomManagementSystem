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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.asura.library.posters.*
import com.asura.library.views.PosterSlider
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Adapter.ImageVideoPagerAdapter
import com.hw.rms.roommanagementsystem.Adapter.NewsPagerAdapter
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import com.hw.rms.roommanagementsystem.Model.ImageVideo
import com.hw.rms.roommanagementsystem.Model.News
import com.hw.rms.roommanagementsystem.R
import java.io.File

class AvailableMainActivity : AppCompatActivity() {

    private var SAMPLE_LONG_TEXT: String = "The quick brown fox jumps over the lazy dog "

    lateinit var tv_running_text : TextView
    lateinit var tv_enter_admin : TextView
    lateinit var btn_status : Button

    lateinit var posterSlider: PosterSlider
    var posters : ArrayList<Poster> = arrayListOf()

    //news
    lateinit var vpNews: ViewPager
    lateinit var newsPagerAdapter: NewsPagerAdapter
    var newsListLeft : MutableList<News> = mutableListOf()
    var newsListRight : MutableList<News> = mutableListOf()
    var handlerNews : Handler? = null
    var ctrNews : Int = 0

    lateinit var vpImageVideo: ViewPager
    lateinit var imageVideoPagerAdapter: ImageVideoPagerAdapter
    var imageVideoList : MutableList<ImageVideo> = mutableListOf()



    private fun hideStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(R.layout.activity_main_available)
        val sharedPref = SharedPreference(this)

        initView()
//        initImageSlider()
        initViewPager()
        initButtonListener()
        imageVideoAutoScroll()

        var jsonString = """{"imageTitle":1,"imagePath":"Test","bijiKuda":"AHSIAPAPPPPPP"}"""

        var gson = Gson()
        var conv = gson.fromJson(jsonString,ImageVideo::class.java)

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
//        posterSlider = findViewById(R.id.poster_slider)
        //news pager
        vpNews = findViewById(R.id.view_pager_news)
        //iv pager
        vpImageVideo = findViewById(R.id.view_pager_iv_vv)

    }

    private fun initViewPager(){

        newsListLeft.add(News("News One","Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc placerat cursus ornare. Integer semper a est ac iaculis. Nunc orci odio, efficitur eget tortor eu, malesuada posuere nibh. Donec id orci quis risus consectetur blandit. Cras aliquet risus dui, quis finibus arcu tincidunt at. Duis ac commodo dolor, nec finibus est. Mauris ut elit ultricies, rutrum sem vel, tempus lorem. Quisque auctor, nulla sit amet tempor commodo, orci eros blandit felis, quis consectetur ante lorem at lorem. Mauris vitae leo dolor. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla sed turpis lobortis, suscipit dui fermentum, fermentum tellus. Nullam sollicitudin augue felis. Donec tincidunt mauris massa, quis pellentesque elit imperdiet et. Morbi iaculis turpis arcu, at interdum ipsum maximus vel. Duis vitae purus semper, ultricies arcu at, faucibus est. Nulla aliquam, libero non posuere auctor, ipsum enim mollis dolor, ac cursus nibh odio sed ligula."))
        newsListRight.add(News("News Two","Content Two"))
        newsListLeft.add(News("News Three","Content Three"))
        newsListRight.add(News("News Four","Content Four"))
        newsListLeft.add(News("News Five", "Content Five"))
        newsListRight.add(News("",""))

        newsPagerAdapter = NewsPagerAdapter( newsListLeft, newsListRight,this )
        vpNews.adapter = newsPagerAdapter

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
//        posters.add(RawVideo(R.raw.tensecvideo))
        posters.add(RemoteVideo(Uri.parse("$dirPath/$fileName")))
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
}
