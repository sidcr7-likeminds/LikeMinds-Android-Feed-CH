package com.likeminds.feedsx.posttypes.model

import androidx.annotation.IntDef

const val IMAGE = 1
const val VIDEO = 2
const val DOCUMENT = 3

@IntDef(
    IMAGE,
    VIDEO,
    DOCUMENT
)
@Retention(AnnotationRetention.SOURCE)
annotation class AttachmentType