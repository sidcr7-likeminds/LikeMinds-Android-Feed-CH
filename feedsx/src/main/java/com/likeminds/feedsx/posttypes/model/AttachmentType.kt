package com.likeminds.feedsx.posttypes.model

import androidx.annotation.IntDef

const val IMAGE = 1
const val VIDEO = 2
const val DOCUMENT = 3
const val LINK = 4
const val ARTICLE = 7

@IntDef(
    IMAGE,
    VIDEO,
    DOCUMENT,
    LINK,
    ARTICLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class AttachmentType