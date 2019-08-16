package com.hw.rms.roommanagementsystem.Data

/**
 * API
 * room_management_system/api/booking/get_config
 * Created Date 15 Aug 2019
 */
data class ResponseConfig(
    var company_address: String? = "UNDEFINED",
    var company_email: String? = "UNDEFINED",
    var company_logo: String? = "UNDEFINED",
    var company_name: String? = "UNDEFINED",
    var company_phone: String? = "UNDEFINED",
    var company_short_description: String? = "UNDEFINED",
    var company_short_description_id: String? = "UNDEFINED",
    var config_auto_checkout: String? = "0",
    var config_background_color_available: String? = "UNDEFINED",
    var config_background_color_occupied: String? = "UNDEFINED",
    var config_background_color_waiting: String? = "UNDEFINED",
    var config_booking_on_apps: String? = "0",
    var config_enable_carousel_beta: String? = "0",
    var config_font_color_device: String? = "#FFFFFF",
    var config_font_size_meeting_title: String? = "30",
    var config_font_size_room_name: String? = "60",
    var config_font_size_time_range: String? = "40",
    var config_interactive: String? = "0",
    var config_is_auto_check_in: String? = "0",
    var config_is_booking_pin: String? = "0",
    var config_is_password: String? = "0",
    var config_request_to_server: String? = "A",
    var config_send_reminder_after: String? = "0",
    var config_show_waiting_time_after: String? = "0"
)