package com.hw.rms.roommanagementsystem.Helper

import com.google.gson.GsonBuilder
import com.hw.rms.roommanagementsystem.Data.ResponseConfig
import com.hw.rms.roommanagementsystem.Data.ResponseRoom
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface API {
    
    @GET("api/configuration/get_data/")
    fun getConfigData() : Call<ResponseConfig>

    @GET("api/configuration/get_room/")
    fun getRoomList() : Call<List<ResponseRoom>>

    companion object Factory{
//        "http://103.82.242.195/room_management_system/"
//        http://139.180.142.76/room_management_system/
        var serverUrl : String? = null
        var socketUrl : String? = null

        fun networkApi() : API{
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(60,TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(serverUrl)!!
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(API::class.java)
        }
    }

}