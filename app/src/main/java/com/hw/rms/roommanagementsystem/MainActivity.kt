package com.hw.rms.roommanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel

class MainActivity : AppCompatActivity() {

    private var SAMPLE_LONG_TEXT: String = "The quick brown fox jumps over the lazy dog "

    lateinit var tv_running_text : TextView
    lateinit var tc_enter_admin : TextClock
    lateinit var btn_status : Button

    private fun hideStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(R.layout.activity_main)
        initView()
        initImageSlider()


    }

    fun initView(){

        //running text below screen
        tv_running_text = findViewById(R.id.tv_running_text)
        tv_running_text.isSelected = true
        tv_running_text.text = SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT + SAMPLE_LONG_TEXT

        //clock
        tc_enter_admin = findViewById(R.id.tc_enter_admin)
        tc_enter_admin.setOnLongClickListener {
            val intent = Intent(this@MainActivity,AdminLoginActivity::class.java)
            startActivity(intent)
            true
        }

        //status
        btn_status = findViewById(R.id.btn_status)


        buttonListener()
    }

    fun initImageSlider(){
        val imageList = ArrayList<SlideModel>()
// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title
// imageList.add(SlideModel("String Url" or R.drawable, "title", true) Also you can add centerCrop scaleType for this image
        imageList.add(SlideModel(R.drawable.fox, centerCrop = true))
        imageList.add(SlideModel(R.drawable.elephant,centerCrop = true))
        imageList.add(SlideModel(R.drawable.tiger, centerCrop = true))
        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)
    }

    fun buttonListener() {

    }
}
