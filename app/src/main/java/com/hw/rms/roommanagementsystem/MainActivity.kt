package com.hw.rms.roommanagementsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.asura.library.posters.DrawableImage
import com.asura.library.posters.Poster
import com.asura.library.posters.RawVideo
import com.asura.library.views.PosterSlider

class MainActivity : AppCompatActivity() {

    private var SAMPLE_LONG_TEXT: String = "The quick brown fox jumps over the lazy dog "

    lateinit var tv_running_text : TextView
    lateinit var tv_enter_admin : TextView
    lateinit var btn_status : Button

    lateinit var posterSlider: PosterSlider
    var posters : ArrayList<Poster> = arrayListOf()

    private fun hideStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(R.layout.activity_main_available)
        initView()
        initImageSlider()


    }

    fun initView(){

        //running text below screen
        tv_running_text = findViewById(R.id.tv_running_text)
        tv_running_text.isSelected = true
        tv_running_text.text = SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT +
                SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT

        //clock
        tv_enter_admin = findViewById(R.id.tv_enter_admin)
        tv_enter_admin.setOnLongClickListener {
            val intent = Intent(this@MainActivity,AdminLoginActivity::class.java)
            startActivity(intent)
            true
        }

        //status
        btn_status = findViewById(R.id.btn_status)


        buttonListener()
    }

    fun initImageSlider(){
        //can be set by server
        posterSlider = findViewById(R.id.poster_slider)
        posters.add(DrawableImage(R.drawable.fox))
        posters.add(RawVideo(R.raw.seven_sec))

        posterSlider.setPosters(posters)

    }

    fun buttonListener() {

    }
}
