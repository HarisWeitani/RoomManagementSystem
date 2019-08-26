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
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.Helper.NetworkConnection


class NoConnectionActivity : AppCompatActivity() {

    lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)
        actionBar?.hide()
        progressBar = spin_kit

        checkConnection()
    }

    fun checkConnection(){
        Handler().postDelayed({
            if( !GlobalVal.isNetworkConnected ) {
                NetworkConnection(this).execute()
                checkConnection()
                Log.d(GlobalVal.NETWORK_TAG, " Connection Failed Try Again ")
            }
            else startActivity(Intent(this@NoConnectionActivity,RootActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        },2000)
    }

}
