package com.likeminds.feedsx.utils.model

import androidx.annotation.IntDef

const val ITEM_NONE = 0
const val ITEM_POST_TEXT_ONLY = 1
const val ITEM_POST_SINGLE_IMAGE = 2
const val ITEM_POST_SINGLE_VIDEO = 3
const val ITEM_POST_DOCUMENTS = 4
const val ITEM_POST_LINK = 5
const val ITEM_POST_MULTIPLE_MEDIA = 6


@IntDef(
    ITEM_NONE,
    ITEM_POST_TEXT_ONLY,
    ITEM_POST_SINGLE_IMAGE,
    ITEM_POST_SINGLE_VIDEO,
    ITEM_POST_DOCUMENTS,
    ITEM_POST_LINK,
    ITEM_POST_MULTIPLE_MEDIA
)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewType