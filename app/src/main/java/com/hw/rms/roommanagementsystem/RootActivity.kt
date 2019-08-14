package com.hw.rms.roommanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.hw.rms.roommanagementsystem.Activity.AvailableMainActivity
import com.hw.rms.roommanagementsystem.AdminActivity.AdminLoginActivity
import com.hw.rms.roommanagementsystem.Helper.GlobalVal
import com.hw.rms.roommanagementsystem.Helper.SharedPreference
import android.R.attr.password
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Model.DummyModel
import com.hw.rms.roommanagementsystem.Model.Json4Kotlin_Base


class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val sharepref = SharedPreference(this)
        val firstInstall = sharepref.getValueBoolean(GlobalVal.FRESH_INSTALL_KEY,true)

        request(firstInstall)

    }
    private fun startActivity( firstInstall: Boolean ){
        Handler().postDelayed({
            if( firstInstall ) startActivity(Intent(this@RootActivity,AdminLoginActivity::class.java))
            else startActivity(Intent(this@RootActivity,AvailableMainActivity::class.java))
        },500)
    }

    private fun request(firstInstall: Boolean){
        val queue = Volley.newRequestQueue(this)
        val url = "http://103.82.242.195/room_management_system/api/booking/get_config"

        val postRequest = object : StringRequest(Method.POST,url,
            Response.Listener {
                var gson = Gson()
                GlobalVal.configModel = gson.fromJson(it, Json4Kotlin_Base::class.java)
                startActivity(firstInstall)
                Log.d("VolleyRequest", it.toString())
            },
            Response.ErrorListener {
                Log.d("VolleyRequest", it.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["room_id"] = "25"

                return params
            }
        }

        queue.add(postRequest)

    }

}
