package com.likeminds.feedsx.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    /**
     * @param milliseconds - This should be the UNIX timestamp
     */
    fun createDateFormat(pattern: String, milliseconds: Long): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date(milliseconds * 1000))
    }

    fun getDateTitleForGallery(date: Long): String {
        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeInMillis = date * 1000
        val calendar = Calendar.getInstance()
        return if (isCurrentWeek(calendar, targetCalendar)) "Recent"
        else {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            if (isPastWeek(calendar, targetCalendar)) "Last Week"
            else {
                calendar.set(Calendar.DAY_OF_WEEK, 1)
                if (isCurrentMonth(calendar, targetCalendar)) "Last Month"
                else getMonthName(date)
            }
        }
    }

    private fun isCurrentWeek(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.WEEK_OF_YEAR] == targetCalendar[Calendar.WEEK_OF_YEAR]
                && calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
    }

    private fun isPastWeek(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.WEEK_OF_YEAR] == targetCalendar[Calendar.WEEK_OF_YEAR]
                && calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
    }

    private fun isCurrentMonth(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
                && calendar[Calendar.MONTH] == targetCalendar[Calendar.MONTH]
                && calendar[Calendar.DAY_OF_MONTH] > targetCalendar[Calendar.DAY_OF_MONTH]
    }

    private fun getMonthName(date: Long): String {
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(date * 1000)
    }
}