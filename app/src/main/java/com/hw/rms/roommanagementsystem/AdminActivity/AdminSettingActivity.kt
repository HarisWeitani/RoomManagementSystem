package com.hw.rms.roommanagementsystem.AdminActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.hw.rms.roommanagementsystem.Data.RequestConfig
import com.hw.rms.roommanagementsystem.Data.ResponseConfig
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import com.hw.rms.roommanagementsystem.R
import com.hw.rms.roommanagementsystem.RootActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    lateinit var btn_try_serverconn : Button
    lateinit var btn_try_socketconn : Button

    var apiService : API = API.networkApi()

    //layout setting
    lateinit var linearlay_other_settings : LinearLayout
    var serverConnected : Boolean = false
    var socketConnected : Boolean = false

    lateinit var socket: Socket

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

        //spinner
        sp_room_name = findViewById(R.id.spinner_room_name)
        sp_server = findViewById(R.id.spinner_server)
        sp_building_name = findViewById(R.id.spinner_building_name)
        sp_socket = findViewById(R.id.spinner_socket)

        //button
        btn_save_and_exit = findViewById(R.id.btn_save_and_exit)
        btnBack = findViewById(R.id.btnBack)

        btn_try_serverconn = findViewById(R.id.btn_try_serverconn)
        btn_try_socketconn = findViewById(R.id.btn_try_socketconn)

        //layout view
        linearlay_other_settings = findViewById(R.id.linearlay_other_settings)
        linearlay_other_settings.visibility = View.GONE

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

        btn_try_serverconn.setOnClickListener {
            connectServer()
        }
        btn_try_socketconn.setOnClickListener {
            connectSocket()
        }
    }

    private fun connectServer(){
        runOnUiThread {
            btn_try_serverconn.text = getString(R.string.connecting)
            btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_yellow))
        }
        apiService.getConfigData(RequestConfig("25")).enqueue(object : Callback<ResponseConfig>{
            override fun onFailure(call: Call<ResponseConfig>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG,t.toString())

                runOnUiThread {
                    btn_try_serverconn.text = getString(R.string.try_connection_server)
                    btn_try_serverconn.background.clearColorFilter()
                    Toast.makeText(this@AdminSettingActivity,"Connection Failed Please Try Again", Toast.LENGTH_LONG).show()
                }
                serverConnected = false
            }
            override fun onResponse(call: Call<ResponseConfig>?, response: Response<ResponseConfig>?) {
                Log.d(GlobalVal.NETWORK_TAG, response!!.body().toString())

                if( response.code() == 200 && response.body() != null ){
                    DAO.configData = response.body()
                    serverConnected = true
                }else{
                    serverConnected = false
                }
            }
        })
        serverConnection()
    }

    private fun serverConnection(){
        if( !serverConnected ){
            Handler().postDelayed({
                serverConnection()
            },2500)
        }else if( serverConnected ){
            runOnUiThread {
                btn_try_serverconn.text = getString(R.string.success)
                btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_green))
                if( socketConnected ) linearlay_other_settings.visibility = View.VISIBLE
            }
        }
    }

    private fun connectSocket() {

        runOnUiThread {
            btn_try_socketconn.text = getString(R.string.connecting)
            btn_try_socketconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_yellow))
        }

        socket = IO.socket("http://192.168.1.10:3000")

        val onNewMessage = Emitter.Listener { args ->
            runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val username: String
                val message: String
                try {
                    username = data.getString("username")
                    message = data.getString("message")
                } catch (e: JSONException) {
                    return@Runnable
                }

                Log.d("socket", "New Message | $username : $message")
            })
        }

        socket.on("new message", onNewMessage)
        socket.connect()
        socketConnection()
    }

    private fun socketConnection(){
        if( !socket.connected() ){
            Handler().postDelayed({
                socketConnection()
            },2500)
        }else if ( socket.connected() ){
            socketConnected = true
            runOnUiThread {
                btn_try_socketconn.text = getString(R.string.success)
                btn_try_socketconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_green))
                if( serverConnected ) linearlay_other_settings.visibility = View.VISIBLE
            }
        }
    }


    override fun onBackPressed() {

    }
}
