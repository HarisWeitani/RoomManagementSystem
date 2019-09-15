package com.hw.rms.roommanagementsystem.Data

data class ResponseAddEvent(
    var data: DataAddEvent? = DataAddEvent(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataAddEvent(
    var created_date: String? = "",
    var description: String? = "",
    var end_time: String? = "",
    var host: String? = "",
    var id: String? = "",
    var location: String? = "",
    var start_time: String? = "",
    var summary: String? = ""
)