package com.ram.local_weather.util

import android.text.format.DateUtils
import android.util.Log
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateConvertor {

    fun convertEpoch(epoch: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("Asia/Kolkata"))
    }

    fun getDayLabel(epoch: Long): String {
        val dateTime = convertEpoch(epoch).toLocalDate()
        val today = LocalDateTime.now().toLocalDate()
        val tomorrow = today.plusDays(1)

        return when (dateTime) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            else -> getMonthAndDate(epoch)
        }
    }

    fun getDateAndTime(epoch: Long): Pair<String, String> {
        val dateTime = convertEpoch(epoch)

        val dateFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.of("Asia/Kolkata"))
        val timeFormatter =
            DateTimeFormatter.ofPattern("hh-mm a").withZone(ZoneId.of("Asia/Kolkata"))

        val dateValue = dateFormatter.format(dateTime)
        val timeValue = timeFormatter.format(dateTime)

        return Pair(dateValue, timeValue)
    }

    fun getMonthAndDate(epoch: Long): String {
        val dateTime = convertEpoch(epoch)
        val dateFormatter =
            DateTimeFormatter.ofPattern("MMM dd").withZone(ZoneId.of("Asia/Kolkata"))

        return dateFormatter.format(dateTime)
    }

    fun getExactMomentAgo(epoch: Long): String {
        val currentMilli = System.currentTimeMillis()

        if (currentMilli < epoch + 59999) {
            return "Just Now"
        }

        return DateUtils.getRelativeTimeSpanString(
            epoch,
            currentMilli,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
    }

    fun getMonthGroup(epoch: Long): String {
        val recordDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault())
        val currentDate = ZonedDateTime.now(ZoneId.systemDefault())
        return when {
            recordDate.year == currentDate.year && recordDate.month == currentDate.month -> {
                "This Month"
            }
            else ->{
                recordDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
//                ""
            }
        }
    }
}