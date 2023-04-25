package com.likeminds.feedsample.utils.mediauploader.model

import androidx.annotation.IntDef

const val IMAGE = 1
const val VIDEO = 2
const val PDF = 3

@IntDef(
    IMAGE,
    VIDEO,
    PDF
)
@Retention(AnnotationRetention.SOURCE)
annotation class FileType