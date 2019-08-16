package com.hw.rms.roommanagementsystem.Helper

import com.google.gson.GsonBuilder
import com.hw.rms.roommanagementsystem.Data.ResponseConfig
import com.hw.rms.roommanagementsystem.Data.RequestConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface API {
    
    @POST("api/booking/get_config")
    fun getConfigData(@Body requestConfig : RequestConfig) : Call<ResponseConfig>

    companion object Factory{
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
                .baseUrl("http://103.82.242.195/room_management_system/")!!
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(API::class.java)
        }
    }

}