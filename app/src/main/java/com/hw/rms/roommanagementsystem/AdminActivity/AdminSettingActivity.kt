package com.hw.rms.roommanagementsystem.AdminActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import com.hw.rms.roommanagementsystem.R
import com.hw.rms.roommanagementsystem.RootActivity

class AdminSettingActivity : AppCompatActivity() {

    lateinit var sp_room_name : Spinner
    lateinit var sp_server : Spinner
    lateinit var sp_building_name : Spinner
    lateinit var sp_socket : Spinner

    var room_name = arrayOf("English", "French", "Spanish", "Hindi", "Russian", "Telugu", "Chinese", "German", "Portuguese", "Arabic", "Dutch", "Urdu", "Italian", "Tamil", "Persian", "Turkish", "Other")
    val http_https = arrayOf("http","https")
    var building_name = arrayOf("JECO","STARBAK","DANKIN DONAT")

    lateinit var btnBack : Button
    lateinit var btn_save_and_exit :Button
    var sharePref : SharedPreference? = null

    private fun hideStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(R.layout.activity_admin_setting)
        sharePref = SharedPreference(this)
        initViews()
        initButtonListener()
        initSpinner()
    }

    private fun initViews(){

        sp_room_name = findViewById(R.id.spinner_room_name)
        sp_server = findViewById(R.id.spinner_server)
        sp_building_name = findViewById(R.id.spinner_building_name)
        sp_socket = findViewById(R.id.spinner_socket)

        btn_save_and_exit = findViewById(R.id.btn_save_and_exit)
        btnBack = findViewById(R.id.btnBack)


    }

    private fun initSpinner(){
        sp_room_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }

        val aaRoomName = ArrayAdapter(this, android.R.layout.simple_spinner_item, room_name)
        aaRoomName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_room_name.adapter = aaRoomName

        sp_server.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

        }
        val aaServer = ArrayAdapter(this, android.R.layout.simple_spinner_item, http_https)
        aaServer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_server.adapter = aaServer

        sp_building_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

        }
        val aaBuildingName = ArrayAdapter(this, android.R.layout.simple_spinner_item, building_name)
        aaBuildingName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_building_name.adapter = aaBuildingName

        sp_socket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

        }
        val aaSocket = ArrayAdapter(this, android.R.layout.simple_spinner_item, http_https)
        aaSocket.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_socket.adapter = aaSocket
    }

    private fun initButtonListener(){


        btnBack.setOnClickListener {
            startActivity(Intent(this@AdminSettingActivity, RootActivity::class.java))
        }

        btn_save_and_exit.setOnClickListener {
            sharePref!!.save(GlobalVal.FRESH_INSTALL_KEY,false)
            startActivity(Intent(this@AdminSettingActivity, RootActivity::class.java))
        }
    }

}
