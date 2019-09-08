package com.hw.rms.roommanagementsystem.Data

data class ResponseGetCurrentMeeting(
    var data: DataGetCurrentMeeting? = DataGetCurrentMeeting(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataGetCurrentMeeting(
    var creator: String? = "",
    var end_dateTime: String? = "",
    var id: String? = "",
    var start_dateTime: String? = "",
    var status: String? = "",
    var summary: String? = ""
)