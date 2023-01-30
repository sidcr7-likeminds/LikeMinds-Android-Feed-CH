package com.likeminds.feedsx.utils

object TimeUtil {

    private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000
    private const val HOUR_IN_MILLIS = 60 * 60 * 1000
    private const val MINUTE_IN_MILLIS = 60 * 1000

    // Sets the time of the post as
    // x min (if days & hours are 0 and min > 0)
    // x h (if days are 0)
    // x d (if days are greater than 1)
    // Just Now (otherwise)
    fun getDaysHoursOrMinutes(timestamp: Long): String {
        val days = (timestamp / DAY_IN_MILLIS).toInt()
        val hours = ((timestamp - (days * DAY_IN_MILLIS)) / HOUR_IN_MILLIS).toInt()
        val minutes =
            ((timestamp - (days * DAY_IN_MILLIS) - (hours * HOUR_IN_MILLIS)) / MINUTE_IN_MILLIS).toInt()
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