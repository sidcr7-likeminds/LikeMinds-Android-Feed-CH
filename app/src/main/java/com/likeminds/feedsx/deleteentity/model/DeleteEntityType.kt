package com.likeminds.feedsx.deleteentity.model

import androidx.annotation.IntDef

const val DELETE_ENTITY_TYPE_POST = 0
const val DELETE_ENTITY_TYPE_COMMENT = 1

@IntDef(
    DELETE_ENTITY_TYPE_POST,
    DELETE_ENTITY_TYPE_COMMENT
)
@Retention(AnnotationRetention.SOURCE)
annotation class DeleteEntityType