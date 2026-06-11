package com.ram.local_weather.util

enum class PREF_KEYS(key: String) {
    PERMISSION_ALREADY_ASKED("permission_already_asked"),
    GPS_ALREADY_ASKED("gps_already_asked"),
    ALREADY_SHOWN("alreadyShown"),
    ALREADY_ASKED_NOTIFICATION_PERMISSION("already_asked_notification_permission"),
    NOTIFICATION_PERMISSION("notification_permission"),
    NOTIFICATION_TOPIC_SUBSCRIPTION("notification_topic_subscription")
}