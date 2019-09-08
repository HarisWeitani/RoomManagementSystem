package com.hw.rms.roommanagementsystem.Data

data class ResponseGetRunningText(
    var created_by: String? = "",
    var created_date: String? = "",
    var edited_by: String? = "",
    var edited_date: String? = "",
    var running_text: String? = "",
    var running_text_id: String? = "",
    var running_text_order: String? = "",
    var running_text_status: String? = ""
)