package com.hw.rms.roommanagementsystem

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
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
    lateinit var tv_error_api : TextView
    var downloadCtr = 0
    var isPermitted: Boolean = false

    var isGetConfig: Boolean = false
    var isGetRunningText: Boolean = false
    var isGetNextMeeting: Boolean = false
    var isGetCurrentMeeting: Boolean = false
    var isGetNewsData: Boolean = false
    var isGetSlideShowData: Boolean = false
    var isDownloadFinish: Boolean = false
    var checkMandatoryCtr = 0

    var isAPIError: Boolean = false
    var errorDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val sharepref = SharedPreference(this)
        firstInstall = sharepref.getValueBoolean(GlobalVal.FRESH_INSTALL_KEY,true)
        DAO.settingsData = Gson().fromJson(sharepref.getValueString(GlobalVal.SETTINGS_DATA_KEY), SettingsData::class.java)
        progressBar = findViewById(R.id.progress_horizontal)
        progressBar.visibility = View.GONE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        tv_error_api = findViewById(R.id.tv_error_api)
        tv_error_api.visibility = View.GONE

        if( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ) {
            isPermitted = true
        }

        errorDialog = Dialog(this@RootActivity)

        initErrorDialog()

//        fileDownloader("http://139.180.142.76/room_management_system/assets/uploads/slideshow/original/video/Petunjuk_Menghadapi_Keadaan_Darurat.mp4", "pidio.mp4")
//        fileDownloader("http://139.180.142.76/room_management_system/assets/uploads/slideshow/original/image/download.jpg","tes.jpg")

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

/*    private fun checkConnection(){
        NetworkConnection(this).execute()
        Handler().postDelayed({
            if( GlobalVal.isNetworkConnected ){
                initApp()
            }else{
                Log.d(GlobalVal.NETWORK_TAG, " Connection Failed Start NoConnectionActivity ")
                waitingForNetwork()
            }
        },500)
    }*/

    private fun waitingForNetwork(){
        startActivity(Intent(this@RootActivity,NoConnectionActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    private fun initApp(){

        if( DAO.settingsData != null ){
            apiService = API.networkApi()
            getConfig()
            getRunningText()
            getNextMeeting()
            getCurrentMeeting()
            getSlideShowData()
            getNewsData()
        }else{
            if(isPermitted){
                firstInstall = true
                startActivity()
            }
        }
    }

    private fun startActivity(){
        Handler().postDelayed({
            if (firstInstall) {
                errorDialog?.dismiss()
                startActivity(Intent(this@RootActivity, AdminLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
            else {
                checkMandatoryData()
            }
        }, 5000)
    }

    override fun onResume() {
        super.onResume()
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ) {
            checkPermission()
        }else if( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ){
            initApp()
        }else{
            startActivity()
        }
    }

    override fun onPause() {
        super.onPause()
        isGetConfig = false
        isGetRunningText = false
        isGetNextMeeting = false
        isGetCurrentMeeting = false
        isGetNewsData = false
        isGetSlideShowData = false
        checkMandatoryCtr = 0
    }

    private fun checkMandatoryData(){
        if( isGetConfig &&
                isGetRunningText &&
                isGetNextMeeting &&
                isGetCurrentMeeting &&
                isGetNewsData &&
                isGetSlideShowData &&
                isDownloadFinish
        ){
            if( isAPIError ){
                tv_error_api.visibility = View.VISIBLE
            }else {
                if( !GlobalVal.isMainActivityStarted ) {
                    startActivity(
                        Intent(
                            this@RootActivity,
                            MainActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }else{
            errorDialog?.show()
            Handler().postDelayed({
                if(!GlobalVal.isMainActivityStarted) {
                    checkMandatoryData()
                    checkMandatoryCtr++
                }
            },500)
        }
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

    private fun getConfig(){

        apiService!!.getConfigData().enqueue(object : Callback<ResponseConfig>{
            override fun onFailure(call: Call<ResponseConfig>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getConfig",t.toString())
                Toast.makeText(this@RootActivity,"get Running Text Failed, Time Out", Toast.LENGTH_LONG).show()
                getConfig()
                isGetConfig = false
            }
            override fun onResponse(call: Call<ResponseConfig>?, response: Response<ResponseConfig>?) {
                GlobalVal.networkLogging("onResponse getConfig",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.configData = response.body()
                    fileDownloader(DAO.configData!!.company_logo.toString(), GlobalVal.LOGO_NAME)
                    isGetConfig = DAO.configData != null
                }else{
                    getConfig()
                    isGetConfig = false
                    Toast.makeText(this@RootActivity,"get Running Text Failed, Response Null", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    private fun getRunningText(){
        apiService!!.getRunningText().enqueue(object : Callback<List<ResponseGetRunningText>>{
            override fun onFailure(call: Call<List<ResponseGetRunningText>>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getRunningText",t.toString())
                Toast.makeText(this@RootActivity,"get Running Text Failed, Time Out", Toast.LENGTH_LONG).show()
                getRunningText()
                isGetRunningText = false
            }

            override fun onResponse(
                call: Call<List<ResponseGetRunningText>>?,
                response: Response<List<ResponseGetRunningText>>?
            ) {
                GlobalVal.networkLogging("onResponse getRunningText",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.runningText = response.body()
                    isGetRunningText = DAO.runningText != null
                }else{
                    getRunningText()
                    isGetRunningText = false
                    Toast.makeText(this@RootActivity,"get Running Text Failed, Response Null", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun getNextMeeting(){

        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_code.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["location"] = body

        apiService!!.getNextMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetNextMeeting>{
            override fun onFailure(call: Call<ResponseGetNextMeeting>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getNextMeeting",t.toString())
                Toast.makeText(this@RootActivity,"get Next Meeting Failed, Time Out", Toast.LENGTH_LONG).show()
                getNextMeeting()
                isGetNextMeeting = false
            }

            override fun onResponse(
                call: Call<ResponseGetNextMeeting>?,
                response: Response<ResponseGetNextMeeting>?
            ) {
                GlobalVal.networkLogging("onResponse getNextMeeting",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.nextMeeting = response.body()
                    isGetNextMeeting = DAO.nextMeeting != null
                }else{
                    getNextMeeting()
                    isGetNextMeeting = false
                    Toast.makeText(this@RootActivity,"get Next Meeting Failed, Response Null", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun getCurrentMeeting(){
        var body = RequestBody.create(MediaType.parse("text/plain"), DAO.settingsData!!.room!!.room_code.toString())
        val requestBodyMap = HashMap<String,RequestBody>()
        requestBodyMap["location"] = body

        apiService!!.getCurrentMeeting(requestBodyMap).enqueue(object : Callback<ResponseGetCurrentMeeting>{
            override fun onFailure(call: Call<ResponseGetCurrentMeeting>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getCurrentMeeting",t.toString())
                Toast.makeText(this@RootActivity,"get On Meeting Failed, Time Out", Toast.LENGTH_LONG).show()
                getCurrentMeeting()
                isGetCurrentMeeting = false
            }

            override fun onResponse(
                call: Call<ResponseGetCurrentMeeting>?,
                response: Response<ResponseGetCurrentMeeting>?
            ) {
                GlobalVal.networkLogging("onResponse getCurrentMeeting",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.currentMeeting = response.body()
                    isGetCurrentMeeting = DAO.currentMeeting != null
                }else{
                    getCurrentMeeting()
                    isGetCurrentMeeting = false
                    Toast.makeText(this@RootActivity,"get On Meeting Failed, Response Null", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun getNewsData(){
//        apiService = API.networkApi()
        apiService!!.getNews().enqueue(object : Callback<ResponseNews> {
            override fun onFailure(call: Call<ResponseNews>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getNewsData",t.toString())
                Toast.makeText(this@RootActivity,"get News Failed, Time Out", Toast.LENGTH_LONG).show()
                getNewsData()
                isGetNewsData = false
            }

            override fun onResponse(
                call: Call<ResponseNews>?,
                response: Response<ResponseNews>?
            ) {
                GlobalVal.networkLogging("onResponse getNewsData",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.newsFeed = response.body()
                    isGetNewsData = DAO.newsFeed != null
                }else{
                    getNewsData()
                    Toast.makeText(this@RootActivity,"get News Failed, Response Null", Toast.LENGTH_LONG).show()
                    isGetNewsData = false
                }
            }
        })

    }

    private fun getSlideShowData(){
        var isDownload = false
        apiService!!.getSlideShowData().enqueue(object : Callback<ResponseSlideShowData>{
            override fun onFailure(call: Call<ResponseSlideShowData>?, t: Throwable?) {
                GlobalVal.networkLogging("onFailure getSlideShowData",t.toString())
                Toast.makeText(this@RootActivity,"get Slide Show Failed, Time Out", Toast.LENGTH_LONG).show()
                getSlideShowData()
                isGetSlideShowData = false
            }
            override fun onResponse(call: Call<ResponseSlideShowData>?, response: Response<ResponseSlideShowData>?) {
                GlobalVal.networkLogging("onResponse getSlideShowData",response?.body().toString())

                if( response?.code() == 200 && response.body() != null ){
                    DAO.slideShowData = response.body()
                    isGetSlideShowData = DAO.slideShowData != null
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
                        if( !isDownload ) {
                            downloadFinish()
                        }
                    }
                }else{
                    isDownloadFinish = true
                    isGetSlideShowData = false
                    Toast.makeText(this@RootActivity,"get Slide Show Failed, Response Null", Toast.LENGTH_LONG).show()
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

        progressBar.visibility = View.VISIBLE
        val dirPath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath).toString()
        PRDownloader.initialize(applicationContext)
        PRDownloader.download(url, dirPath, fileName)
            .build()
            .setOnStartOrResumeListener {
                downloadCtr++
                GlobalVal.networkLogging("setOnStartOrResumeListener fileDownloader","$fileName")
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
//                    if( error?.responseCode == 0 ){
//                        GlobalVal.isNetworkConnected = false
//                        waitingForNetwork()
//                    }
                    GlobalVal.networkLogging("onError fileDownloader","${error?.responseCode} $url $fileName")
                    downloadFinish()
                }

                override fun onDownloadComplete() {
                    GlobalVal.networkLogging("onDownloadComplete fileDownloader","$url $fileName")
                    downloadFinish()
                }
            })
    }
    private fun downloadFinish(){
        downloadCtr --
        if( downloadCtr <= 0 && isGetSlideShowData ){
            isDownloadFinish = true
            startActivity()
        }
    }

    fun initErrorDialog(){
        errorDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        errorDialog?.setCancelable(false)
        errorDialog?.setContentView(R.layout.error_dialog)
    }


    override fun onBackPressed() {}
}
