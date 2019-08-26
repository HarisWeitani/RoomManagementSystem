package com.hw.rms.roommanagementsystem.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.hw.rms.roommanagementsystem.R
import com.hw.rms.roommanagementsystem.RootActivity
import kotlinx.android.synthetic.main.activity_no_connection.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.annimon.stream.operator.IntArray
import com.hw.rms.roommanagementsystem.Helper.DAO


class NoConnectionActivity : AppCompatActivity() {

    lateinit var progressBar : ProgressBar

    companion object{
        var isConnected : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)
        actionBar?.hide()
        progressBar = spin_kit

        checkConnection()
    }

    fun checkConnection(){
        Handler().postDelayed({
            if( !isConnected ) {
                IsURLReachable(this).execute()
                checkConnection()
            }
            else startActivity(Intent(this@NoConnectionActivity,RootActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        },5000)
    }

    class IsURLReachable(internal var context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean? {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected) {
                try {
                    val url = URL(DAO.settingsData!!.server_url)   // Change to "http://google.com" for www  test.
                    val urlc = url.openConnection() as HttpURLConnection
                    urlc.connectTimeout = 5 * 1000          // 10 s.
                    urlc.connect()
                    if (urlc.responseCode == 200) {        // 200 = "OK" code (http connection is fine).
                        Log.d("Connection", "Success !")
                        return true
                    } else {
                        Log.d("Connection", "Failed !")
                        return false
                    }
                } catch (e1: MalformedURLException) {
                    return false
                } catch (e: IOException) {
                    return false
                }

            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            isConnected = result!!
            Log.d("Connection", "$result ")
        }
    }


}
