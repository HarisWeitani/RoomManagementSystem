package com.hw.rms.roommanagementsystem.Helper

import com.hw.rms.roommanagementsystem.Data.ConfigData
import com.hw.rms.roommanagementsystem.Data.RequestConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


class NetworkHelper {
    constructor(){

    }

    companion object{
        var serverUrl : String? = null

        const val configAPI : String = "api/booking/get_config"
    }




}