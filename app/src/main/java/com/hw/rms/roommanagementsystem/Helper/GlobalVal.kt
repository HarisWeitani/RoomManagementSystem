package com.hw.rms.roommanagementsystem.Helper

import android.util.Log

class GlobalVal {
    companion object{
        /**
         * TAG LOGGING
         */
        const val NETWORK_TAG = "RMS_NETWORK"
        const val SOCKET_TAG = "RMS_SOCKET"

        /**
         * SHARED PREFERENCES KEY
         */
        const val FRESH_INSTALL_KEY = "FRESH_INSTALL_KEY"
        const val SETTINGS_DATA_KEY = "SETTINGS_DATA_KEY"

        /**
         * Naming
         */
        const val LOGO_NAME ="imageview_logo.png"

        /***
         * Other
         */
        var isNetworkConnected : Boolean = false
        var isSocketConnected : Boolean = false
        var isMainActivityStarted : Boolean = false
        var isSurveyShowed: Boolean = false

        var isVideoStarted: Boolean = false
        var isRefreshMeetingStatus: Boolean = false
        var booking_status: String = "available"


        fun networkLogging(tag : String, result : String){
            Log.d(NETWORK_TAG, "$tag = $result")
        }
    }
}