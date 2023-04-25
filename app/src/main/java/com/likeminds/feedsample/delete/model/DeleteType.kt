package com.likeminds.feedsample.delete.model

import androidx.annotation.IntDef

const val DELETE_TYPE_POST = 0
const val DELETE_TYPE_COMMENT = 1

@IntDef(
    DELETE_TYPE_POST,
    DELETE_TYPE_COMMENT
)
@Retention(AnnotationRetention.SOURCE)
annotation class DeleteType