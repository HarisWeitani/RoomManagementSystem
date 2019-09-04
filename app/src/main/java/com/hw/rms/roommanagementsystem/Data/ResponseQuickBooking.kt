package com.hw.rms.roommanagementsystem.Data

data class ResponseQuickBooking(
    var data: DataQuickBooking? = DataQuickBooking(),
    var booking_id: Int? = 0,
    var message: String? = "",
    var ok: Int? = 0
)

data class DataQuickBooking(
    var additional_package: String? = "",
    var booking_date: String? = "",
    var booking_id: String? = "",
    var booking_pin: String? = "",
    var booking_status: String? = "",
    var booking_time_end: String? = "",
    var booking_time_start: String? = "",
    var created_by: String? = "",
    var created_date: String? = "",
    var edited_by: String? = "",
    var edited_date: String? = "",
    var meeting_status: String? = "",
    var meeting_title: String? = "",
    var member_address: String? = "",
    var member_class: String? = "",
    var member_email: String? = "",
    var member_first_name: String? = "",
    var member_gender: String? = "",
    var member_id: String? = "",
    var member_last_name: String? = "",
    var member_password: String? = "",
    var member_phone: String? = "",
    var member_status: String? = "",
    var member_username: String? = "",
    var room_available_day: String? = "",
    var room_available_national_holiday: Any? = Any(),
    var room_available_time: String? = "",
    var room_capacity: String? = "",
    var room_class: String? = "",
    var room_code: String? = "",
    var room_custom_closed_date: Any? = Any(),
    var room_custom_open_date: Any? = Any(),
    var room_id: String? = "",
    var room_is_private: String? = "",
    var room_name: String? = "",
    var room_status: String? = "",
    var special_request: String? = "",
    var total_participant: String? = ""
)