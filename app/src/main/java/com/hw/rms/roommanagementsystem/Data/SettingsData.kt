package com.hw.rms.roommanagementsystem.Data

import com.hw.rms.roommanagementsystem.Data.Old.ResponseRoom

data class SettingsData(
    var server_url : String? = "",
    var socket_url : String? = "",
    var server_full_url : String? = "",
    var socket_full_url : String? = "",
    var building_name : String? = "Building Name",
    var serial_number : String? = "Serial Number",
    var license : String? = "License",
    var room : ResponseRoom? = ResponseRoom(
        "0",
        "0",
        "No Room",
        "0"
    ),
    var isScreenAlwaysOn : Boolean? = true,
    var admin_pin : String? = "1111"
)