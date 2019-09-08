package com.hw.rms.roommanagementsystem.Data

data class SettingsData(
    var server_url : String? = "",
    var socket_url : String? = "",
    var server_full_url : String? = "",
    var socket_full_url : String? = "",
    var serial_number : String? = "Serial Number",
    var license : String? = "License",
    var room : DataGetAllRooms? = DataGetAllRooms(
        "0",
        "0",
        "No Room",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0"
    ),
    var building : DataGetAllBuildings? = DataGetAllBuildings(
        "0",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0",
        "0"
    ),
    var isScreenAlwaysOn : Boolean? = true,
    var admin_pin : String? = "1111"
)