package com.hw.rms.roommanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.downloader.Error
import com.downloader.PRDownloader
import com.hw.rms.roommanagementsystem.Activity.AvailableMainActivity
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.github.nkzawa.socketio.client.IO
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import com.github.nkzawa.emitter.Emitter
import com.downloader.OnDownloadListener
import com.hw.rms.roommanagementsystem.Data.*
import com.hw.rms.roommanagementsystem.Helper.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File

class RootActivity : AppCompatActivity() {

    var firstInstall : Boolean = true
    var apiService : API? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val sharepref = SharedPreference(this)
        firstInstall = sharepref.getValueBoolean(GlobalVal.FRESH_INSTALL_KEY,true)
        DAO.settingsData = Gson().fromJson(sharepref.getValueString(GlobalVal.SETTINGS_DATA_KEY), SettingsData::class.java)

        if( DAO.settingsData != null ){
            apiService = API.networkApi()
            getNextMeeting()
            getOnMeeting()
            getNewsData()
        }else{
            startActivity()
        }

//        fileDownloader("http://139.180.142.76/room_management_system/assets/uploads/slideshow/original/video/Petunjuk_Menghadapi_Keadaan_Darurat.mp4", "pidio.mp4")
//        fileDownloader("http://139.180.142.76/room_management_system/assets/uploads/slideshow/original/image/download.jpg","tes.jpg")

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
                response: retrofit2.Response<ResponseGetNextMeeting>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response!!.body().toString())
                if( response.code() == 200 && response.body() != null ){
                    DAO.nextMeeting = response.body()
                }else{

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
                Toast.makeText(this@RootActivity,"get Next Meeting Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseGetOnMeeting>?,
                response: retrofit2.Response<ResponseGetOnMeeting>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response!!.body().toString())
                if( response.code() == 200 && response.body() != null ){
                    DAO.onMeeting = response.body()
                }else{

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
                response: retrofit2.Response<ResponseNews>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG, response!!.body().toString())

                if( response.code() == 200 && response.body() != null ){
                    DAO.newsFeed = response.body()
                }else{

                }
                startActivity()
            }

        })
    }

    private fun startActivity(){

        Handler().postDelayed({
            if( firstInstall ) startActivity(Intent(this@RootActivity,AdminLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            else startActivity(Intent(this@RootActivity,AvailableMainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        },500)
    }

    private fun socketConnection(){
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

        val disconnect = Emitter.Listener { args ->
            runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val username: String
                val numUsers: String
                try {
                    username = data.getString("username")
                    numUsers = data.getString("numUsers")
                } catch (e: JSONException) {
                    return@Runnable
                }

                Log.d("socket", "Disconnect | $username : $numUsers")
            })
        }

        var socket = IO.socket("http://192.168.1.10:3000")
        socket.on("new message", onNewMessage)
        socket.on("user left", disconnect)
        socket.connect()
        Log.d("socket", "status ${socket.connected()}")
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
                    Log.d(GlobalVal.NETWORK_TAG,"fileDownloader onDownloadComplete")
                }
            })
    }

}
