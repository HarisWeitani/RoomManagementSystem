package com.hw.rms.roommanagementsystem.Data

data class ResponseGetAllRooms(
    var data: List<DataGetAllRooms?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataGetAllRooms(
    var building_id: String? = "",
    var created_by: String? = "",
    var created_date: String? = "",
    var edited_by: String? = "",
    var edited_date: String? = "",
    var room_capacity: String? = "",
    var room_code: String? = "",
    var room_config: String? = "",
    var room_id: String? = "",
    var room_name: String? = "",
    var room_status: String? = ""
)