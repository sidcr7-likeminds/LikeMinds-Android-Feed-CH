package com.likeminds.feedsx.report.model

import androidx.annotation.IntDef

const val REPORT_TYPE_POST = 0
const val REPORT_TYPE_COMMENT = 1

@IntDef(
    REPORT_TYPE_POST,
    REPORT_TYPE_COMMENT
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportType
