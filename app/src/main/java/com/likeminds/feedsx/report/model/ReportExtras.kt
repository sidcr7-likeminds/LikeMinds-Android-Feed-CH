package com.likeminds.feedsx.report.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportExtras private constructor(
    @ReportType
    var entityType: Int,
    var entityCreatorId: String,
    var entityId: String,
    val parentCommentId: String?,
    var postId: String,
    var postViewType: Int?,
) : Parcelable {
    class Builder {
        @ReportType
        private var entityType: Int = REPORT_TYPE_POST
        private var entityCreatorId: String = ""
        private var entityId: String = ""
        private var parentCommentId: String? = null
        private var postId: String = ""
        private var postViewType: Int? = null

        fun entityType(@ReportType entityType: Int) = apply { this.entityType = entityType }
        fun entityCreatorId(entityCreatorId: String) =
            apply { this.entityCreatorId = entityCreatorId }

        fun entityId(entityId: String) = apply { this.entityId = entityId }
        fun parentCommentId(parentCommentId: String?) =
            apply { this.parentCommentId = parentCommentId }

        fun postId(postId: String) = apply { this.postId = postId }
        fun postViewType(postViewType: Int?) = apply { this.postViewType = postViewType }

        fun build() = ReportExtras(
            entityType,
            entityCreatorId,
            entityId,
            parentCommentId,
            postId,
            postViewType
        )
    }

    fun toBuilder(): Builder {
        return Builder().entityCreatorId(entityCreatorId)
            .entityType(entityType)
            .entityId(entityId)
            .parentCommentId(parentCommentId)
            .postId(postId)
            .postViewType(postViewType)
    }
}