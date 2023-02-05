package com.likeminds.feedsx.deletecontent.model

import androidx.annotation.IntDef

const val DELETE_CONTENT_TYPE_POST = 0
const val DELETE_CONTENT_TYPE_COMMENT = 1

@IntDef(
    DELETE_CONTENT_TYPE_POST,
    DELETE_CONTENT_TYPE_COMMENT
)
@Retention(AnnotationRetention.SOURCE)
annotation class DeleteContentType