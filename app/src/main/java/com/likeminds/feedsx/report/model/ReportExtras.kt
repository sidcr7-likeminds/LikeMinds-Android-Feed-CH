package com.likeminds.feedsx.report.model

import android.os.Parcelable
import com.likeminds.feedsx.posttypes.model.PostViewData
import kotlinx.parcelize.Parcelize

// todo: pass post
@Parcelize
class ReportExtras private constructor(
    @ReportType
    var entityType: Int,
    var entityCreatorId: String,
    var entityId: String,
    val parentCommentId: String?,
    var post: PostViewData
) : Parcelable {
    class Builder {
        @ReportType
        private var entityType: Int = REPORT_TYPE_POST
        private var entityCreatorId: String = ""
        private var entityId: String = ""
        private var parentCommentId: String? = null
        private var post: PostViewData = PostViewData.Builder().build()

        fun entityType(@ReportType entityType: Int) = apply { this.entityType = entityType }
        fun entityCreatorId(entityCreatorId: String) =
            apply { this.entityCreatorId = entityCreatorId }

        fun entityId(entityId: String) = apply { this.entityId = entityId }
        fun parentCommentId(parentCommentId: String?) =
            apply { this.parentCommentId = parentCommentId }

        fun post(post: PostViewData) = apply { this.post = post }

        fun build() = ReportExtras(
            entityType,
            entityCreatorId,
            entityId,
            parentCommentId,
            post
        )
    }

    fun toBuilder(): Builder {
        return Builder().entityCreatorId(entityCreatorId)
            .entityType(entityType)
            .entityId(entityId)
            .parentCommentId(parentCommentId)
            .post(post)
    }
}