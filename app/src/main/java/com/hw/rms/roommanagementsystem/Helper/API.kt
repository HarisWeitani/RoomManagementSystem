package com.hw.rms.roommanagementsystem.Helper

import com.google.gson.GsonBuilder
import com.hw.rms.roommanagementsystem.Data.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface API {

//  testing
//    https://www.googleapis.com/calendar/v3/calendars/calendarId/events

    @GET("calendar/v3/calendars/hweitani@gmail.com/events")
    fun getCalendar() : Call<ResponseConfig>

    @GET("api/configuration/get_data/")
    fun getConfigData() : Call<ResponseConfig>

    @GET("api/configuration/get_room/")
    fun getRoomList() : Call<List<ResponseRoom>>

    @GET("api/newsfeed/get_data/")
    fun getNews() : Call<ResponseNews>

    @GET("api/slideshow/get_data/")
    fun getSlideShowData() : Call<ResponseSlideShowData>

    @Multipart
    @POST("api/booking/get_next_meeting/")
    fun getNextMeeting(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetNextMeeting>

    @Multipart
    @POST("api/booking/get_on_meeting/")
    fun getOnMeeting(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetOnMeeting>

    companion object Factory{
//        "http://103.82.242.195/room_management_system/"
//        http://139.180.142.76/room_management_system/
        var serverUrl : String? = DAO.settingsData?.server_full_url
        var socketUrl : String? = DAO.settingsData?.server_full_url

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

        fun googleApi() : API{
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(60,TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")!!
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(API::class.java)
        }
    }

}