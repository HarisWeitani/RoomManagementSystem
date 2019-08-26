package com.hw.rms.roommanagementsystem.AdminActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.R
import com.hw.rms.roommanagementsystem.RootActivity
import java.text.SimpleDateFormat
import java.util.*

class AdminLoginActivity : AppCompatActivity() {

    lateinit var et_admin_pin : EditText
    lateinit var btnBack : Button
    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        actionBar?.hide()
        initView()
    }

    private fun initView(){
        //admin pin
        et_admin_pin = findViewById(R.id.et_admin_pin)
        btnBack = findViewById(R.id.btnBack)
        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)
        initDateTime()
        initButtonListener()

    }

    private fun initDateTime(){
        val date = Date()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        val clockFormat = SimpleDateFormat("HH:mm")
        runOnUiThread{
            tv_date.text = dateFormat.format(date)
            tv_clock.text = clockFormat.format(date)
        }
    }
    private fun initButtonListener(){
        var pinNow = DAO.settingsData?.admin_pin
        if( pinNow == null ) pinNow = "1111"

        et_admin_pin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if( s.toString() == pinNow ) {
                    finish()
                    startActivity(Intent(this@AdminLoginActivity, AdminSettingActivity::class.java))
                }
                else if ( s.toString().length == 4)
                    Toast.makeText(this@AdminLoginActivity,"Wrong Code", Toast.LENGTH_SHORT).show()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        btnBack.setOnClickListener {
            startActivity(Intent(this@AdminLoginActivity,RootActivity::class.java))
        }
    }
}
