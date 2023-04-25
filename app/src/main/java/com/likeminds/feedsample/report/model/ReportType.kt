package com.likeminds.feedsample.report.model

import androidx.annotation.IntDef

const val REPORT_TYPE_POST = 5
const val REPORT_TYPE_COMMENT = 6
const val REPORT_TYPE_REPLY = 7

@IntDef(
    REPORT_TYPE_POST,
    REPORT_TYPE_COMMENT,
    REPORT_TYPE_REPLY
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportType {
    companion object {
        //return entity type in string for id
        fun getEntityType(@ReportType type: Int): String {
            return when (type) {
                REPORT_TYPE_COMMENT -> "Comment"
                REPORT_TYPE_POST -> "Post"
                REPORT_TYPE_REPLY -> "Reply"
                else -> ""
            }
        }
    }
}
