package com.hw.rms.roommanagementsystem.Helper

import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.GsonBuilder
import com.hw.rms.roommanagementsystem.Data.*
import com.hw.rms.roommanagementsystem.Data.Old.ResponseGetNextMeeting
import com.hw.rms.roommanagementsystem.Data.Old.ResponseGetOnMeeting
import com.hw.rms.roommanagementsystem.Data.Old.ResponseRoom
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface API {

    @GET("api/configuration/get_data/")
    fun getConfigData() : Call<ResponseConfig>

    @GET("api/configuration/get_room/")
    fun getRoomList() : Call<List<ResponseRoom>>

    @GET("api/newsfeed/get_data/")
    fun getNews() : Call<ResponseNews>

    @GET("api/slideshow/get_data/")
    fun getSlideShowData() : Call<ResponseSlideShowData>

    /***
     * Versi 1
     */
    @Multipart
    @POST("api/booking/get_next_meeting/")
    fun getNextMeeting(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetNextMeeting>

    @Multipart
    @POST("api/booking/get_on_meeting/")
    fun getOnMeeting(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetOnMeeting>

//    @Multipart
//    @POST("google/booking/add_event")
//    fun googleAddEvent(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<String>

    @Multipart
    @POST("google/booking/add_event")
    fun googleAddEvent(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseAddEvent>

    @Multipart
    @POST("google/booking/upcoming_events")
    fun googleUpcomingEvent(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseUpcomingEvent>

    @Multipart
    @POST("google/booking/get_event_by_date")
    fun getEventByDate(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseScheduleByDate>

    companion object Factory{

//        http://139.180.142.76/room_management_system

        var serverUrl : String? = DAO.settingsData?.server_full_url
        var socketUrl : String? = DAO.settingsData?.server_full_url

        lateinit var socket : Socket

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
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            return retrofit.create(API::class.java)
        }

        fun socketIO(){
            socket = IO.socket(socketUrl)
            socket.on("global_message_from_master", globalMessageFromMaster() )
            socket.on("private_message_from_master", privateMessageFromMaster() )
            socket.on("disconnect", disconnect() )
            socket.on("reconnect", reconnect() )
            socket.on("reconnect_error", reconnectError() )
            socket.connect()
        }

        fun joinUserSocket(){
            socket.emit("user_join_mobile","aki_gendeng")
        }

        fun isSocketConnected() : Boolean = socket.connected()

        fun releaseSocketIo(){
            socket.disconnect()
            socket.off("global_message_from_master", globalMessageFromMaster() )
            socket.off("private_message_from_master", privateMessageFromMaster() )
            socket.off("disconnect", disconnect() )
            socket.off("reconnect", reconnect() )
            socket.off("reconnect_error", reconnectError() )
        }

        private fun disconnect() : Emitter.Listener{
            return Emitter.Listener { args ->
                GlobalVal.isSocketConnected = false
                Log.d(GlobalVal.SOCKET_TAG, "Disconnected From Server")
            }
        }

        private fun globalMessageFromMaster() : Emitter.Listener{
            return Emitter.Listener { args ->
                val data = args[0] as JSONObject
                val username: String
                val message: String

                username = data.getString("username")
                message = data.getString("message")

                Log.d(GlobalVal.SOCKET_TAG, "global_message_from_master | $username : $message")
            }
        }
        private fun privateMessageFromMaster() : Emitter.Listener{
            return Emitter.Listener { args ->
                val data = args[0] as JSONObject
                val username: String
                val message: String

                username = data.getString("username")
                message = data.getString("message")

                Log.d(GlobalVal.SOCKET_TAG, "private_message_from_master | $username : $message")
            }
        }

        private fun reconnect() : Emitter.Listener{
            return Emitter.Listener { args ->
                joinUserSocket()
                GlobalVal.isSocketConnected = true
                Log.d(GlobalVal.SOCKET_TAG, "Reconnected From Server")
            }
        }

        private fun reconnectError() : Emitter.Listener{
            return Emitter.Listener { args ->
                GlobalVal.isSocketConnected = false
                Log.d(GlobalVal.SOCKET_TAG, "Reconnected Error ")
            }
        }


    }

}