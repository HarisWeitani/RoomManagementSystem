package com.hw.rms.roommanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AdminLoginActivity : AppCompatActivity() {

    lateinit var et_admin_pin : EditText
    lateinit var btnBack : Button

    private fun hideStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(R.layout.activity_admin_login)
        initView()

    }

    fun initView(){
        //admin pin
        et_admin_pin = findViewById(R.id.et_admin_pin)
        et_admin_pin.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString() == "1111")
                        startActivity(Intent(this@AdminLoginActivity,AdminSettingActivity::class.java))
                    else if ( s.toString().length == 4)
                        Toast.makeText(this@AdminLoginActivity,"THOU SHALL NOT PASS", Toast.LENGTH_SHORT).show()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            }
        )

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            super.onBackPressed()
        }
    }
}
