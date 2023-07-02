package com.likeminds.feedsx.report.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportExtras private constructor(
    @ReportType
    val entityType: Int,
    val uuid: String,
    val entityId: String,
    val parentCommentId: String?,
    val postId: String,
    val postViewType: Int?,
) : Parcelable {
    class Builder {
        @ReportType
        private var entityType: Int = REPORT_TYPE_POST
        private var uuid: String = ""
        private var entityId: String = ""
        private var parentCommentId: String? = null
        private var postId: String = ""
        private var postViewType: Int? = null

        fun entityType(@ReportType entityType: Int) = apply { this.entityType = entityType }
        fun uuid(uuid: String) =
            apply { this.uuid = uuid }

        fun entityId(entityId: String) = apply { this.entityId = entityId }
        fun parentCommentId(parentCommentId: String?) =
            apply { this.parentCommentId = parentCommentId }

        fun postId(postId: String) = apply { this.postId = postId }
        fun postViewType(postViewType: Int?) = apply { this.postViewType = postViewType }

        fun build() = ReportExtras(
            entityType,
            uuid,
            entityId,
            parentCommentId,
            postId,
            postViewType
        )
    }

    fun toBuilder(): Builder {
        return Builder().uuid(uuid)
            .entityType(entityType)
            .entityId(entityId)
            .parentCommentId(parentCommentId)
            .postId(postId)
            .postViewType(postViewType)
    }
}