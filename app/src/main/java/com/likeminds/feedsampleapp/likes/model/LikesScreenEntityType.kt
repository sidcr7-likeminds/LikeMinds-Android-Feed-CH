package com.likeminds.feedsampleapp.likes.model

import androidx.annotation.IntDef

const val POST = 0
const val COMMENT = 1

@IntDef(
    POST,
    COMMENT
)

@Retention
annotation class LikesScreenEntityType