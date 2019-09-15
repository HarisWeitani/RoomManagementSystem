package com.hw.rms.roommanagementsystem.Helper

import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.GsonBuilder
import com.hw.rms.roommanagementsystem.Data.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import java.util.concurrent.TimeUnit

interface API {
    /***
     * Versi 1
     */
    @GET("api/newsfeed/get_data/")
    fun getNews() : Call<ResponseNews>

    @GET("api/slideshow/get_data/")
    fun getSlideShowData() : Call<ResponseSlideShowData>

    /***
     * Versi 2
     */

    @Multipart
    @POST("google/booking/get_events_by_date/")
    fun getEventByDate(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseScheduleByDate>

    @Multipart
    @POST("google/booking/get_current_event/")
    fun getCurrentMeeting(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetCurrentMeeting>

    @Multipart
    @POST("google/booking/get_upcoming_events")
    fun getNextMeeting(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetNextMeeting>

    @Multipart
    @POST("google/booking/add_event")
    fun googleAddEvent(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseAddEvent>

    @Multipart
    @POST("google/booking/extend_event")
    fun googleExtendEvent(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseExtendEvent>

    @GET("api/running_text/get_data/")
    fun getRunningText() : Call<List<ResponseGetRunningText>>

    @GET("api/configuration/get_data/")
    fun getConfigData() : Call<ResponseConfig>

    @GET("google/resource/get_all_buildings")
    fun getAllBuildings() : Call<ResponseGetAllBuildings>

    @Multipart
    @POST("google/resource/get_all_rooms")
    fun getRoomList(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetAllRooms>

    @Multipart
    @POST("google/survey/add_survey")
    fun addSurvey(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseSurvey>

    @Multipart
    @POST("google/booking/check_out")
    fun manualCheckOut(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseCheckOut>

    @Multipart
    @POST("google/booking/auto_check_out")
    fun autoCheckOut(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseCheckOut>

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
                .baseUrl(serverUrl)!! //crash
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            return retrofit.create(API::class.java)
        }

        /***
         * Unused
         */
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