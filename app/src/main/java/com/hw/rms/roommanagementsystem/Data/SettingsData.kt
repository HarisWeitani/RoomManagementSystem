package com.hw.rms.roommanagementsystem.Data

data class SettingsData(
    var server_url : String? = "",
    var socket_url : String? = "",
    var building_name : String? = "Building Name",
    var serial_number : String? = "Serial Number",
    var license : String? = "License",
    var room : ResponseRoom? = ResponseRoom("0","0","No Room", "0")
)