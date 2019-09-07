package com.hw.rms.roommanagementsystem.Data

data class ResponseUpcomingEvent(
    var data: List<DataUpcomingEvent?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataUpcomingEvent(
    var creator: String? = "",
    var end_dateTime: String? = "",
    var start_dateTime: String? = "",
    var status: String? = "",
    var summary: String? = ""
)