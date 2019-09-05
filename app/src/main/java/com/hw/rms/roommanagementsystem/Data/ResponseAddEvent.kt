package com.hw.rms.roommanagementsystem.Data

data class ResponseAddEvent(
    var data: List<Any?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)