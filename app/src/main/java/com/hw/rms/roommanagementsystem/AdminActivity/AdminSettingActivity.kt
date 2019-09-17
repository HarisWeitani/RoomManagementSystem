package com.hw.rms.roommanagementsystem.AdminActivity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Adapter.SpinnerRoomAdapter
import com.hw.rms.roommanagementsystem.Adapter.SpinnerBuildingAdapter
import com.hw.rms.roommanagementsystem.Data.*
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import com.hw.rms.roommanagementsystem.R
import com.hw.rms.roommanagementsystem.RootActivity
import kotlinx.android.synthetic.main.activity_admin_setting.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AdminSettingActivity : AppCompatActivity() {

    lateinit var sp_room_name : Spinner
    lateinit var sp_server : Spinner
    lateinit var sp_building_name : Spinner
    lateinit var sp_socket : Spinner

    lateinit var tvSocketConnectionStatus : TextView

    val http_https = arrayOf("http","https")
    var building_name = arrayOf("JECO","STARBAK","DANKIN DONAT")

    lateinit var btnBack : LinearLayout
    lateinit var btn_save_and_exit :Button
    var sharePref : SharedPreference? = null

    lateinit var btn_try_serverconn : Button
    lateinit var btn_try_socketconn : Button

    lateinit var et_server_url : EditText
    lateinit var et_socket_url : EditText

    lateinit var etChangeAdminPin : EditText
    lateinit var screenOnSwitch : Switch

    lateinit var tvDeviceSerialNumber : TextView

    var apiService : API? = null
    lateinit var socket: Socket

    //layout setting
    lateinit var linearlay_other_settings : LinearLayout
    var serverConnected : Boolean = false
    var socketConnected : Boolean = false
    var isFirstInitSpinner: Boolean = true

    var serverProtocol : String? = null
    var socketProtocol : String? = null

    var roomName : String? = null
    var buildingName : String? = null
    var serverUrl : String? = null
    var socketUrl : String? = null

    var selectedRoom : DataGetAllRooms? = null
    var selectedBuilding : DataGetAllBuildings? = null

    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_setting)
        actionBar?.hide()
        sharePref = SharedPreference(this)
        initViews()
        try {
            checkIfScreenAlwaysOn()
        }catch (e : Exception){}
    }
    private fun checkIfScreenAlwaysOn(){
        if( DAO.settingsData?.isScreenAlwaysOn!! ) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    private fun initViews(){

        tvSocketConnectionStatus = tv_socket_connection_status
        if( GlobalVal.isSocketConnected ) tvSocketConnectionStatus.text = "Socket Is Connected"
        else tvSocketConnectionStatus.text = "Socket Is Not Connected"

        //admin pin
        etChangeAdminPin = et_change_admin_pin

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

        et_server_url = findViewById(R.id.et_server_url)
        et_socket_url = findViewById(R.id.et_socket_url)

        et_server_url.setText(DAO.settingsData?.server_url)
        et_socket_url.setText(DAO.settingsData?.socket_url)

        //switch
        screenOnSwitch = screen_on_switch
        var bool = DAO.settingsData?.isScreenAlwaysOn
        if( bool == null ) bool = true
        screenOnSwitch.isChecked = bool

        //layout view
        linearlay_other_settings = findViewById(R.id.linearlay_other_settings)
        linearlay_other_settings.visibility = View.GONE

        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)

        //serial number
        tvDeviceSerialNumber = tv_device_serial_number
        val imeiDevice : String? =
            if( getImeiDevice() != null){
                getImeiDevice()
            }else{
                "Unknown"
            }

        tvDeviceSerialNumber.text = imeiDevice

        initDateTime()
        initButtonListener()
        initUrlSpinner()
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
    private fun initUrlSpinner(){
        val aaServer = ArrayAdapter(this, android.R.layout.simple_spinner_item, http_https)
        aaServer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_server.adapter = aaServer
        sp_server.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                serverProtocol = http_https[position]
            }

        }

        val aaSocket = ArrayAdapter(this, android.R.layout.simple_spinner_item, http_https)
        aaSocket.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_socket.adapter = aaSocket
        sp_socket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                socketProtocol = http_https[position]
            }

        }
    }

    private fun initSpinner(){
        val aaRoomName = SpinnerRoomAdapter(this, android.R.layout.simple_spinner_item,
            DAO.roomList?.data as List<DataGetAllRooms>
        )
        aaRoomName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_room_name.adapter = aaRoomName
        sp_room_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//               roomName = room_name[position]
                selectedRoom = parent!!.selectedItem as DataGetAllRooms?
            }
        }

        if( isFirstInitSpinner ) {
            isFirstInitSpinner = false
            val aaBuildingName = SpinnerBuildingAdapter(
                this, android.R.layout.simple_spinner_item,
                DAO.buildingList?.data as List<DataGetAllBuildings>
            )
            aaBuildingName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_building_name.adapter = aaBuildingName
            sp_building_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (selectedBuilding != null && selectedBuilding != parent!!.selectedItem as DataGetAllBuildings?) {
                        selectedBuilding = parent!!.selectedItem as DataGetAllBuildings?
                        try {
                            getRoomListData(selectedBuilding?.building_id!!)
                        } catch (e: Exception) {
                            Crashlytics.logException(e)
                            Toast.makeText(
                                this@AdminSettingActivity,
                                "Get Room List Fail Please Restart App",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else if (selectedBuilding == null) {
                        selectedBuilding = parent!!.selectedItem as DataGetAllBuildings?
                    }
                }

            }
        }
    }

    private fun getImeiDevice() : String? {
        val  telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is  granted
            val imei : String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            } else { // older OS  versions
                telephonyManager.deviceId
            }

            imei?.let {
                Log.i("imei", "DeviceId=$imei" )
            }

            return imei?.substring(imei.length - 8 )
        } else {  // Permission is not granted
            Toast.makeText(this@AdminSettingActivity, "READ PHONE STATE is Not Permitted", Toast.LENGTH_LONG).show()
            return null
        }
    }

    private fun initButtonListener(){

        btnBack.setOnClickListener {
            startActivity(Intent(this@AdminSettingActivity, RootActivity::class.java))
        }

        btn_save_and_exit.setOnClickListener {
            var roomListSize = 0
            try {
                roomListSize = DAO.roomList?.data!!.size
            }catch (e : Exception){
                Crashlytics.logException(e)
            }
            if( roomListSize > 0 ) {
                var pin = DAO.settingsData?.admin_pin
                if (etChangeAdminPin.text.toString().length == 4) {
                    pin = etChangeAdminPin.text.toString()
                } else if (etChangeAdminPin.text.toString().isNotEmpty()) {
                    Toast.makeText(
                        this@AdminSettingActivity,
                        " Pin Not Accepted ",
                        Toast.LENGTH_LONG
                    ).show()
                }

                DAO.settingsData =
                    SettingsData(
                        server_full_url = API.serverUrl,
                        socket_full_url = API.socketUrl,
                        server_url = serverUrl,
                        socket_url = socketUrl,
                        isScreenAlwaysOn = screenOnSwitch.isChecked,
                        admin_pin = pin,
                        room = selectedRoom,
                        building = selectedBuilding
                    )
                val settingDataJson = Gson().toJson(DAO.settingsData)

                sharePref!!.save(GlobalVal.FRESH_INSTALL_KEY, false)
                sharePref!!.save(GlobalVal.SETTINGS_DATA_KEY, settingDataJson)

                startActivity(
                    Intent(this@AdminSettingActivity, RootActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                )
            }else{
                Toast.makeText(this@AdminSettingActivity,"Please Choose A Room", Toast.LENGTH_LONG).show()
            }
        }

        btn_try_serverconn.setOnClickListener {
            if( et_server_url.text.length > 10 ){
//                API.serverUrl = "$serverProtocol://${et_server_url.text}/room_management_system/"
                API.serverUrl = "$serverProtocol://${et_server_url.text}/"
                serverUrl = et_server_url.text.toString()
                connectServer()
            }else{
                Toast.makeText(this@AdminSettingActivity,"Wrong Url",Toast.LENGTH_LONG).show()
            }
        }
        btn_try_socketconn.setOnClickListener {
            if( et_socket_url.text.length > 10 ){
//                  "http://192.168.1.10:3000"
                API.socketUrl = "$socketProtocol://${et_socket_url.text}"
                socketUrl = et_socket_url.text.toString()
                connectSocket()
            }
        }

        screenOnSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("switch", " $isChecked ")
        }
    }
    private fun connectServer(){
        runOnUiThread {
            btn_try_serverconn.text = getString(R.string.connecting)
            btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_yellow))
        }
        apiService = API.networkApi()
        apiService!!.getConfigData().enqueue(object : Callback<ResponseConfig>{
            override fun onFailure(call: Call<ResponseConfig>?, t: Throwable?) {
                GlobalVal.networkLogging("connectServer onFailure",t.toString())
                runOnUiThread {
                    btn_try_serverconn.text = getString(R.string.failed)
                    btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_red))
                    Toast.makeText(this@AdminSettingActivity,"Connection Failed Please Try Again", Toast.LENGTH_LONG).show()
                }
                serverConnected = false
            }
            override fun onResponse(call: Call<ResponseConfig>?, response: Response<ResponseConfig>?) {
                GlobalVal.networkLogging("connectServer onResponse",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.configData = response.body()
                    getBuildingList()
                    fileDownloader(DAO.configData!!.company_logo.toString(), GlobalVal.LOGO_NAME)
                }else{
                    serverConnected = false
                }
            }
        })
    }
    private fun getBuildingList(){
        apiService!!.getAllBuildings().enqueue(object : Callback<ResponseGetAllBuildings>{
            override fun onFailure(call: Call<ResponseGetAllBuildings>?, t: Throwable?) {
                GlobalVal.networkLogging("getBuildingList onFailure",t.toString())
            }

            override fun onResponse(
                call: Call<ResponseGetAllBuildings>?,
                response: Response<ResponseGetAllBuildings>?
            ) {
                GlobalVal.networkLogging("connectServer onResponse",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.buildingList = response.body()
                    getRoomListData(DAO.buildingList?.data?.get(0)?.building_id!!)
                }else{

                }
            }

        })
    }
    private fun refreshRoomList(){

    }
    private fun getRoomListData(buildingId : String){
        var body = RequestBody.create(MediaType.parse("text/plain"), buildingId)
        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["building_id"] = body

        apiService!!.getRoomList(requestBodyMap).enqueue(object : Callback<ResponseGetAllRooms>{
            override fun onFailure(call: Call<ResponseGetAllRooms>?, t: Throwable?) {
                GlobalVal.networkLogging("getRoomListData onFailure",t.toString())

                runOnUiThread {
                    btn_save_and_exit.text = getString(R.string.try_connection_server)
                    btn_save_and_exit.background.clearColorFilter()
                    Toast.makeText(this@AdminSettingActivity,"Fetching Data Room List Failed", Toast.LENGTH_LONG).show()
                }
                serverConnected = false
            }

            override fun onResponse(call: Call<ResponseGetAllRooms>?, response: Response<ResponseGetAllRooms>?) {
                GlobalVal.networkLogging("getRoomListData onResponse",response?.body().toString())

                if( response?.code() == 200 && response.body() != null ){
                    DAO.roomList = response.body()
                    if(!serverConnected) {
                        serverConnected = true
                        serverConnected()
                    }
                    initSpinner()
                }else{
                    serverConnected = false
                }
            }
        })
    }

    private fun serverConnected(){
        runOnUiThread {
            btn_try_serverconn.text = getString(R.string.success)
            btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_green))
            linearlay_other_settings.visibility = View.VISIBLE
            et_server_url.isEnabled = false
        }
    }

    private fun connectSocket() {
        runOnUiThread {
            btn_try_socketconn.text = getString(R.string.connecting)
            btn_try_socketconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_yellow))
        }
        API.socketIO()
        socketConnection()
    }

    var tryAttempt  = 0
    private fun socketConnection(){
        if( tryAttempt > 4 ){
            runOnUiThread {
                btn_try_socketconn.text = getString(R.string.failed)
                btn_try_socketconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_red))
            }
        }
        else if( !API.isSocketConnected() ){
            Handler().postDelayed({
                socketConnection()
                tryAttempt++
            },2500)
        }else if ( API.isSocketConnected() ){
            socketConnected = true
            GlobalVal.isSocketConnected = true
            API.joinUserSocket()
            runOnUiThread {
                tvSocketConnectionStatus.text = "Socket Is Connected"
                btn_try_socketconn.text = getString(R.string.success)
                btn_try_socketconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_green))
            }
        }
    }

    private fun fileDownloader(url : String, fileName : String){
        val dirPath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath).toString()
        PRDownloader.initialize(applicationContext)
        PRDownloader.download(url, dirPath, fileName)
            .build()
            .setOnStartOrResumeListener { }
            .setOnProgressListener { }
            .start(object : OnDownloadListener {
                override fun onError(error: Error?) {
                    Log.d(GlobalVal.NETWORK_TAG,"fileDownloader on Error ${error?.responseCode} | ${error?.serverErrorMessage} ")
                }

                override fun onDownloadComplete() {
                    Log.d(GlobalVal.NETWORK_TAG,"fileDownloader complete $url \n $fileName")
                }
            })
    }

    override fun onBackPressed() {

    }
}
