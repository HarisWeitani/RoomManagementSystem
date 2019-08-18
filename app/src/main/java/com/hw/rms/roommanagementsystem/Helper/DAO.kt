package com.hw.rms.roommanagementsystem.Helper

import com.hw.rms.roommanagementsystem.Data.ResponseConfig
import com.hw.rms.roommanagementsystem.Data.ResponseRoom
import com.hw.rms.roommanagementsystem.Data.SettingsData

/**
 * DATA ACCESS OBJECT
 */
class DAO {
    companion object{
        /**
         * API Object
         */
        var configData : ResponseConfig? = null
        var roomList : List<ResponseRoom>? = null

        /**
         * Not API Object
         */
        var settingsData : SettingsData? = null
    }
}