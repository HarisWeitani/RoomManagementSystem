package com.hw.rms.roommanagementsystem.Data

data class ResponseSlideShowData(
    var data: List<DataSlideShow?>? = listOf(),
    var ok: Int? = 0,
    var duration: Int? = 5000
)

data class DataSlideShow(
    var slideshow: String? = "",
    var slideshow_id: String? = "",
    var slideshow_name: String? = "",
    var slideshow_type: String? = ""
)