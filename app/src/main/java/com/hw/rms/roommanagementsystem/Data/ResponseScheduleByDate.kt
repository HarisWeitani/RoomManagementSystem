package com.hw.rms.roommanagementsystem.Data

/***
 * Update 09 Sept 2019 Add id
 */
data class ResponseScheduleByDate(
    var data: List<DataScheduleByDate?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataScheduleByDate(
    var creator: String? = "",
    var end_dateTime: String? = "",
    var start_dateTime: String? = "",
    var status: String? = "",
    var summary: String? = "",
    var id: String? = ""
)