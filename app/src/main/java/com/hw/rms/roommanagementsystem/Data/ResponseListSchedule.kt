package com.hw.rms.roommanagementsystem.Data

data class ResponseListSchedule(
    var data: List<DataListSchedule>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataListSchedule(
    var end_time: String? = "",
    var meeting_title: String? = "",
    var room_name: String? = "",
    var start_time: String? = "",
    var status: String? = ""
)