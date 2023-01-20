package com.likeminds.feedsx.utils

object TimeUtil {

    private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000
    private const val MILLIS_IN_HOURS = 60 * 60 * 1000
    private const val MILLIS_IN_MINUTES = 60 * 1000

    fun getDaysHoursOrMinutes(timestamp: Long): String {
        val days = (timestamp / MILLIS_IN_DAY).toInt()
        val hours = ((timestamp - (days * MILLIS_IN_DAY)) / MILLIS_IN_HOURS).toInt()
        val minutes =
            ((timestamp - (days * MILLIS_IN_DAY) - (hours * MILLIS_IN_HOURS)) / MILLIS_IN_MINUTES).toInt()
        return when {
            days == 0 && hours == 0 && minutes > 0 -> "$minutes min"
            days == 0 && hours == 1 -> "$hours h"
            days == 0 && hours > 1 -> "$hours h"
            days == 1 && hours == 0 -> "$days d"
            days >= 1 -> "$days d"
            else -> "Just Now"
        }
    }

}