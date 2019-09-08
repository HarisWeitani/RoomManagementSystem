package com.hw.rms.roommanagementsystem.Data

data class ResponseGetAllBuildings(
    var `data`: List<DataGetAllBuildings?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)

data class DataGetAllBuildings(
    var building_code: String? = "",
    var building_config: String? = "",
    var building_id: String? = "",
    var building_name: String? = "",
    var building_order: String? = "",
    var building_status: String? = "",
    var created_by: Any? = Any(),
    var created_date: Any? = Any(),
    var edited_by: Any? = Any(),
    var edited_date: Any? = Any()
)