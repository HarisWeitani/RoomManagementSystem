package com.hw.rms.roommanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.downloader.Error
import com.downloader.PRDownloader
import com.hw.rms.roommanagementsystem.Activity.AvailableMainActivity
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import com.github.nkzawa.socketio.client.IO
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import com.github.nkzawa.emitter.Emitter
import com.hw.rms.roommanagementsystem.Data.ConfigData
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.rms.roommanagementsystem.Helper.NetworkHelper
import com.downloader.OnDownloadListener
import com.downloader.Progress
import com.downloader.OnProgressListener
import com.downloader.OnPauseListener
import com.downloader.OnStartOrResumeListener




class RootActivity : AppCompatActivity() {

    var firstInstall : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val sharepref = SharedPreference(this)
        firstInstall = sharepref.getValueBoolean(GlobalVal.FRESH_INSTALL_KEY,true)

        postRequest()
//        startActivity()
//        socketConnection()
    }
    private fun startActivity(){
        Handler().postDelayed({
            if( firstInstall ) startActivity(Intent(this@RootActivity,AdminLoginActivity::class.java))
            else startActivity(Intent(this@RootActivity,AvailableMainActivity::class.java))
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
        socket.connected()
    }

    private fun postRequest(){
        val queue = Volley.newRequestQueue(this)

        val url = "http://103.82.242.195/room_management_system/${NetworkHelper.configAPI}"

        val postRequest = object : StringRequest(Method.POST,url,
            Response.Listener {
                var gson = Gson()
                DAO.configData = gson.fromJson(it, ConfigData::class.java)
                startActivity()
                Log.d(GlobalVal.NETWORK_TAG, it.toString())
            },
            Response.ErrorListener {
                Log.d(GlobalVal.NETWORK_TAG, "Error ${it.networkResponse}")
            }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params["room_id"] = "25"

                return params
            }
        }
        queue.add(postRequest)
    }

    private fun fileDownloader(url : String, dirPath : String, fileName : String){

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
