package com.hw.rms.roommanagementsystem.Data

data class ResponseGetNextMeeting(
    var data: List<DataGetNextMeeting?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataGetNextMeeting(
    var creator: String? = "",
    var end_dateTime: String? = "",
    var start_dateTime: String? = "",
    var status: String? = "",
    var summary: String? = "",
    var id: String? = ""
)