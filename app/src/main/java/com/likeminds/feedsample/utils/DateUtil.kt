package com.likeminds.feedsample.utils

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

    /**
     * @param date - This should be in seconds
     * Generates the date header for gallery
     */
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

    // Returns if the queried date is in current week or not
    private fun isCurrentWeek(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.WEEK_OF_YEAR] == targetCalendar[Calendar.WEEK_OF_YEAR]
                && calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
    }

    // Returns if the queried date is in past week or not
    private fun isPastWeek(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.WEEK_OF_YEAR] == targetCalendar[Calendar.WEEK_OF_YEAR]
                && calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
    }

    // Returns if the queried date is in current month or not
    private fun isCurrentMonth(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
                && calendar[Calendar.MONTH] == targetCalendar[Calendar.MONTH]
                && calendar[Calendar.DAY_OF_MONTH] > targetCalendar[Calendar.DAY_OF_MONTH]
    }

    /**
     * @param date - This should be in seconds
     * @return - Full name of the month of provided date
     */
    private fun getMonthName(date: Long): String {
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(date * 1000)
    }
}