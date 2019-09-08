package com.hw.rms.roommanagementsystem.Data


/**
 * API
 * Created Date 15 Aug 2019 api/booking/get_config
 * Updated Date 18 Aug 2019 api/configuration/get_data/
 * Update Date 09 Sept 2019 Add config_timestamp , config_show_survey_before
 */
data class ResponseConfig(
    var company_address: String? = "",
    var company_email: String? = "",
    var company_logo: String? = "",
    var company_name: String? = "",
    var company_phone: String? = "",
    var company_short_description: String? = "",
    var company_short_description_en: String? = "",
    var config_auto_checkin: String? = "",
    var config_auto_checkout: String? = "",
    var config_background_color_available: String? = "",
    var config_background_color_occupied: String? = "",
    var config_background_color_waiting: String? = "",
    var config_booking_automatic_approval: String? = "",
    var config_booking_automatic_approval_admin: String? = "",
    var config_booking_enable_additional_package: String? = "",
    var config_booking_enable_facility: String? = "",
    var config_booking_enable_package: String? = "",
    var config_booking_for_member_only: String? = "",
    var config_booking_on_apps: String? = "",
    var config_enable_carousel_beta: String? = "",
    var config_font_color_device: String? = "",
    var config_font_size_meeting_title: String? = "",
    var config_font_size_room_name: String? = "",
    var config_font_size_time_range: String? = "",
    var config_interactive: String? = "",
    var config_is_booking_pin: String? = "",
    var config_is_password: String? = "",
    var config_request_to_server: String? = "",
    var config_send_reminder_after: String? = "",
    var config_show_waiting_time_after: String? = "",
    var config_timestamp: String? = "", //For Extend Time
    var config_show_survey_before: String? = ""
)