package com.hw.rms.roommanagementsystem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.downloader.Error
import com.downloader.PRDownloader
import com.hw.rms.roommanagementsystem.Activity.MainActivity
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.google.gson.Gson
import com.downloader.OnDownloadListener
import com.hw.rms.roommanagementsystem.Activity.NoConnectionActivity
import com.hw.rms.roommanagementsystem.Data.*
import com.hw.rms.roommanagementsystem.Data.Old.ResponseGetNextMeeting
import com.hw.rms.roommanagementsystem.Data.Old.ResponseGetOnMeeting
import com.hw.rms.roommanagementsystem.Helper.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RootActivity : AppCompatActivity() {

    var firstInstall : Boolean = true
    var apiService : API? = null
    var EXTERNAL_REQUEST = 0
    var INTERNET_REQUEST = 1
    var READ_PHONE_STATE = 2
    lateinit var progressBar : ProgressBar
    var downloadCtr = 0
    var isPermitted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val sharepref = SharedPreference(this)
        firstInstall = sharepref.getValueBoolean(GlobalVal.FRESH_INSTALL_KEY,true)
        DAO.settingsData = Gson().fromJson(sharepref.getValueString(GlobalVal.SETTINGS_DATA_KEY), SettingsData::class.java)
        progressBar = findViewById(R.id.progress_horizontal)
        progressBar.visibility = View.GONE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

//        fileDownloader("http://139.180.142.76/room_management_system/assets/uploads/slideshow/original/video/Petunjuk_Menghadapi_Keadaan_Darurat.mp4", "pidio.mp4")
//        fileDownloader("http://139.180.142.76/room_management_system/assets/uploads/slideshow/original/image/download.jpg","tes.jpg")

    }

    override fun onResume() {
        super.onResume()
        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 ) {
            checkPermission()
        }else{
            if( firstInstall ) startActivity(Intent(this@RootActivity,AdminLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            else checkConnection()
        }
    }

    private fun checkPermission() {

        var internet = false
        var storage = false
        var readPhoneState = false

        if( ContextCompat.checkSelfPermission(this@RootActivity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this@RootActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET),INTERNET_REQUEST)
        }else{
            internet = true
        }

        if(
            ContextCompat.checkSelfPermission(this@RootActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this@RootActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this@RootActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),EXTERNAL_REQUEST)
        }else{
            storage = true
        }

        if (ContextCompat.checkSelfPermission(this@RootActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@RootActivity, arrayOf(Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_STATE),READ_PHONE_STATE)
        } else {  // Permission is not granted
            readPhoneState = true
        }

        isPermitted = internet && storage && readPhoneState

        if( isPermitted ){
            initApp()
        }
    }

    private fun checkConnection(){
        NetworkConnection(this).execute()
        Handler().postDelayed({
            if( GlobalVal.isNetworkConnected ){
                initApp()
            }else{
                Log.d(GlobalVal.NETWORK_TAG, " Connection Failed Start NoConnectionActivity ")
                waitingForNetwork()
            }
        },500)
    }

    private fun waitingForNetwork(){
        startActivity(Intent(this@RootActivity,NoConnectionActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    private fun initApp(){

        if( DAO.settingsData != null ){
            apiService = API.networkApi()
            getNextMeeting()
            getUpcomingEvents()
            getOnMeeting()
            getSlideShowData()
            getNewsData()
        }else{
            if(isPermitted) startActivity()
        }
    }

    private fun startActivity(){

        Handler().postDelayed({
            if( firstInstall ) startActivity(Intent(this@RootActivity,AdminLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            else startActivity(Intent(this@RootActivity,MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        },500)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            EXTERNAL_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermission()
                }
                return
            }

            INTERNET_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermission()
                }
                return
            }

            READ_PHONE_STATE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermission()
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getNextMeeting(){

        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["room_id"] = body

        apiService!!.getNextMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetNextMeeting>{
            override fun onFailure(call: Call<ResponseGetNextMeeting>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@RootActivity,"get Next Meeting Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseGetNextMeeting>?,
                response: Response<ResponseGetNextMeeting>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.nextMeeting = response.body()
                }else{
                    Toast.makeText(this@RootActivity,"get Next Meeting Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getUpcomingEvents(){
        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["location_id"] = body

        apiService!!.googleUpcomingEvent(requestBodyMap).enqueue(object : Callback<ResponseUpcomingEvent>{
            override fun onFailure(call: Call<ResponseUpcomingEvent>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@RootActivity,"get Upcoming Events Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseUpcomingEvent>?,
                response: Response<ResponseUpcomingEvent>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.upcomingEvent = response.body()
                }else{
                    Toast.makeText(this@RootActivity,"get Upcoming Events Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getOnMeeting(){
        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_id.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["room_id"] = body

        apiService!!.getOnMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetOnMeeting>{
            override fun onFailure(call: Call<ResponseGetOnMeeting>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@RootActivity,"get On Meeting Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseGetOnMeeting>?,
                response: Response<ResponseGetOnMeeting>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.onMeeting = response.body()
                }else{
                    Toast.makeText(this@RootActivity,"get On Meeting Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getNewsData(){
//        apiService = API.networkApi()
        apiService!!.getNews().enqueue(object : Callback<ResponseNews> {
            override fun onFailure(call: Call<ResponseNews>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@RootActivity,"get News Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseNews>?,
                response: Response<ResponseNews>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())

                if( response?.code() == 200 && response.body() != null ){
                    DAO.newsFeed = response.body()
                }else{
                    Toast.makeText(this@RootActivity,"get News Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun getSlideShowData(){
        var isDownload = false
        apiService!!.getSlideShowData().enqueue(object : Callback<ResponseSlideShowData>{
            override fun onFailure(call: Call<ResponseSlideShowData>?, t: Throwable?) {

                Log.d(GlobalVal.NETWORK_TAG, t.toString())
                Toast.makeText(this@RootActivity,"get Slide Show Failed", Toast.LENGTH_SHORT).show()

            }
            override fun onResponse(call: Call<ResponseSlideShowData>?, response: Response<ResponseSlideShowData>?) {
                Log.d(GlobalVal.NETWORK_TAG, response?.body().toString())

                if( response?.code() == 200 && response.body() != null ){
                    DAO.slideShowData = response.body()
                    //compare ke DB dlu
                    if( DAO.slideShowData!!.data!!.isNotEmpty() ) {
                        for (x in 0 until DAO.slideShowData!!.data!!.size){

                            val dataTemp = DAO.slideShowData!!.data!![x]

                            if( dataTemp!!.slideshow_type.equals("1") ){
                                val filename = "${dataTemp.slideshow_id}${dataTemp.slideshow_name}.png"
                                if( !checkIfFileExist(filename) ) {
                                    isDownload = true
                                    fileDownloader(dataTemp.slideshow!!,filename)
                                }
                            }
                            else if (dataTemp.slideshow_type.equals("2") ){
                                val filename = "${dataTemp.slideshow_id}${dataTemp.slideshow_name}.mp4"
                                if( !checkIfFileExist(filename) ) {
                                    isDownload = true
                                    fileDownloader(dataTemp.slideshow!!,filename)
                                }
                            }

                        }
                        if( !isDownload ) startActivity()
                        Log.d(GlobalVal.NETWORK_TAG,"No File Downloaded")
                    }
                }else{
                    Toast.makeText(this@RootActivity,"get Slide Show Failed", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    private fun checkIfFileExist( name : String ) : Boolean{
        val filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        var file = File("$filePath/$name")
        return file.exists()
    }

    private fun fileDownloader(url : String, fileName : String){
        downloadCtr ++
        progressBar.visibility = View.VISIBLE
        val dirPath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath).toString()
        PRDownloader.initialize(applicationContext)
        PRDownloader.download(url, dirPath, fileName)
            .build()
            .setOnStartOrResumeListener {
                Log.d(GlobalVal.NETWORK_TAG,"fileDownloader Start $fileName ")
            }
            .setOnProgressListener {
                var current = it.currentBytes
                var total = it.totalBytes
                var progress = ((current*100)/total).toInt()
                runOnUiThread {
                    progressBar.progress = progress
                }
            }
            .start(object : OnDownloadListener {
                override fun onError(error: Error?) {
                    //error 0 kalau ditengah jalan inet putus
                    if( error?.responseCode == 0 ){
                        GlobalVal.isNetworkConnected = false
                        waitingForNetwork()
                    }
                    Log.d(GlobalVal.NETWORK_TAG,"fileDownloader on Error ${error?.responseCode} $url $fileName ")
                    downloadFinish()
                }

                override fun onDownloadComplete() {
                    Log.d(GlobalVal.NETWORK_TAG,"fileDownloader complete $url $fileName")
                    downloadFinish()
                }
            })
    }
    private fun downloadFinish(){
        downloadCtr --
        if( downloadCtr == 0 ){
            Handler().postDelayed({
                startActivity()
            },500)
        }
    }

}
