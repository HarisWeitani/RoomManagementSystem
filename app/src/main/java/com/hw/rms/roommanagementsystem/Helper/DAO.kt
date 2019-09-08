package com.hw.rms.roommanagementsystem.Helper

import com.hw.rms.roommanagementsystem.Data.*
import com.hw.rms.roommanagementsystem.Data.Old.ResponseGetNextMeeting
import com.hw.rms.roommanagementsystem.Data.Old.ResponseGetOnMeeting
import com.hw.rms.roommanagementsystem.Data.Old.ResponseRoom

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
        var newsFeed : ResponseNews? = null
        var nextMeeting : ResponseGetNextMeeting? = null
        var onMeeting : ResponseGetOnMeeting? = null
        var slideShowData : ResponseSlideShowData? = null
        var scheduleEventByDate : ResponseScheduleByDate? = null
        var upcomingEvent : ResponseUpcomingEvent? = null

        /**
         * Not API Object
         */
        var settingsData : SettingsData? = null
    }
}