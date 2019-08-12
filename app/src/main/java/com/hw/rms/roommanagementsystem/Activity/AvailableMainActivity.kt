package com.hw.rms.roommanagementsystem.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.asura.library.posters.DrawableImage
import com.asura.library.posters.Poster
import com.asura.library.posters.RawVideo
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.hw.rms.roommanagementsystem.Adapter.NewsPagerAdapter
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import com.hw.rms.roommanagementsystem.Model.News
import com.hw.rms.roommanagementsystem.R

class AvailableMainActivity : AppCompatActivity() {

    private var SAMPLE_LONG_TEXT: String = "The quick brown fox jumps over the lazy dog "

    lateinit var tv_running_text : TextView
    lateinit var tv_enter_admin : TextView
    lateinit var btn_status : Button

    lateinit var posterSlider: PosterSlider
    var posters : ArrayList<Poster> = arrayListOf()

    lateinit var mViewPager: ViewPager
    lateinit var newsPagerAdapter: NewsPagerAdapter

    var newsListLeft : MutableList<News> = mutableListOf()
    var newsListRight : MutableList<News> = mutableListOf()

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
        Log.i("Ahsiap",sharedPref.getValueString("name"))

        initView()
        initImageSlider()
        initViewPager()
        initButtonListener()

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
        mViewPager = findViewById(R.id.view_pager_one)

    }


    private fun initViewPager(){

        newsListLeft.add(News("News One","Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc placerat cursus ornare. Integer semper a est ac iaculis. Nunc orci odio, efficitur eget tortor eu, malesuada posuere nibh. Donec id orci quis risus consectetur blandit. Cras aliquet risus dui, quis finibus arcu tincidunt at. Duis ac commodo dolor, nec finibus est. Mauris ut elit ultricies, rutrum sem vel, tempus lorem. Quisque auctor, nulla sit amet tempor commodo, orci eros blandit felis, quis consectetur ante lorem at lorem. Mauris vitae leo dolor. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla sed turpis lobortis, suscipit dui fermentum, fermentum tellus. Nullam sollicitudin augue felis. Donec tincidunt mauris massa, quis pellentesque elit imperdiet et. Morbi iaculis turpis arcu, at interdum ipsum maximus vel. Duis vitae purus semper, ultricies arcu at, faucibus est. Nulla aliquam, libero non posuere auctor, ipsum enim mollis dolor, ac cursus nibh odio sed ligula."))
        newsListRight.add(News("News Two","Content Two"))
        newsListLeft.add(News("News Three","Content Three"))
        newsListRight.add(News("News Four","Content Four"))
        newsListLeft.add(News("News Five", "Content Five"))
        newsListRight.add(News("",""))

        newsPagerAdapter = NewsPagerAdapter( newsListLeft, newsListRight,this )
        mViewPager.adapter = newsPagerAdapter
    }


    private fun initImageSlider(){

        posters.add(DrawableImage(R.drawable.fox))
        posters.add(RawVideo(R.raw.seven_sec))
        posters.add(RemoteVideo(Uri.parse("https://www.youtube.com/watch?v=lTTajzrSkCw")))

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
