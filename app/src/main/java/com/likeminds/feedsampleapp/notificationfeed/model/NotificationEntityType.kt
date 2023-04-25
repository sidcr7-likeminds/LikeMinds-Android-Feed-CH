package com.likeminds.feedsampleapp.notificationfeed.model

import androidx.annotation.StringDef

const val POST = "POST"
const val COMMENT = "COMMENT"
const val USER = "USER"

@StringDef(
    POST,
    COMMENT,
    USER
)

@Retention
annotation class NotificationEntityType