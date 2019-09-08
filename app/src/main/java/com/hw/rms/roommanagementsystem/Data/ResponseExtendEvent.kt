package com.hw.rms.roommanagementsystem.Data

data class ResponseExtendEvent(
    var data: List<Any?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)