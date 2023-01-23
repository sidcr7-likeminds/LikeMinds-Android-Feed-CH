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
}