package com.hw.rms.roommanagementsystem.Data

data class ResponseNews(
    var data : List<DataNews?>? = listOf(),
    var ok: Int? = 0
)

data class DataNews(
    var newsfeed_content: String? = "",
    var newsfeed_id: String? = "",
    var newsfeed_title: String? = ""
)