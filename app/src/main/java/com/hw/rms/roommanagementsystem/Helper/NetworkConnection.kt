package com.hw.rms.roommanagementsystem.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.Log
import com.hw.rms.roommanagementsystem.Activity.NoConnectionActivity
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class NetworkConnection(internal var context: Context) : AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg params: Void?): Boolean? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) {
            try {
                val url = URL("http://"+DAO.settingsData.server_url)   // Change to "http://google.com" for www  test.
                val urlc = url.openConnection() as HttpURLConnection
                urlc.connectTimeout = 2 * 1000          // 10 s.
                urlc.connect()
                if (urlc.responseCode == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.d(GlobalVal.NETWORK_TAG, "Success !")
                    return true
                } else {
                    Log.d(GlobalVal.NETWORK_TAG, "Failed !")
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
        GlobalVal.isNetworkConnected = result!!
        Log.d(GlobalVal.NETWORK_TAG, "$result ")
    }
}