package com.likeminds.feedsample.utils

import androidx.work.Data

fun Data.getIntOrNull(key: String): Int? {
    val id = getInt(key, -1)
    return if (id == -1) {
        null
    } else {
        id
    }
}

fun Data.getLongOrNull(key: String): Long? {
    val id = getLong(key, -1)
    return if (id == -1L) {
        null
    } else {
        id
    }
}