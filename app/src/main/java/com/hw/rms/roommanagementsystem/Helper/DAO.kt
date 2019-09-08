package com.hw.rms.roommanagementsystem.Helper

import com.hw.rms.roommanagementsystem.Data.*

/**
 * DATA ACCESS OBJECT
 */
class DAO {
    companion object{
        /**
         * API Object
         */
        var configData : ResponseConfig? = null
        var roomList : ResponseGetAllRooms? = null
        var newsFeed : ResponseNews? = null
        var nextMeeting : ResponseGetNextMeeting? = null
        var currentMeeting : ResponseGetCurrentMeeting? = null
        var slideShowData : ResponseSlideShowData? = null
        var scheduleEventByDate : ResponseScheduleByDate? = null
        var runningText : List<ResponseGetRunningText>? = null
        var buildingList : ResponseGetAllBuildings? = null

        /**
         * Not API Object
         */
        var settingsData : SettingsData? = null
    }
}