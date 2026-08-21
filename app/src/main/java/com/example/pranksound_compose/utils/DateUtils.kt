package com.example.pranksound.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.isToday(): Boolean {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()
    today.time = this

    return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}

fun Date.isYesterday(): Boolean {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1) // Lùi 1 ngày về hôm qua

    val yesterday = Calendar.getInstance()
    yesterday.time = this

    return calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
}

fun getDateTimeFromMillis(millis: Long, pattern: String, locale: Locale = Locale.getDefault()): String {
    val sdf = SimpleDateFormat(pattern, locale)
    return sdf.format(Date(millis))
}
