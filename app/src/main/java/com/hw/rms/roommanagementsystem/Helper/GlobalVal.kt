package com.hw.rms.roommanagementsystem.Helper

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
    }
}