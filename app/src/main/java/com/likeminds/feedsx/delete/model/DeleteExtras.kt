package com.likeminds.feedsx.delete.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// TODO: confirm about tagId
@Parcelize
class DeleteExtras private constructor(
    @DeleteType
    var entityType: Int,
    var postId: String,
    var commentId: String?,
    var tagId: String?,
    var reason: String?
) : Parcelable {

    class Builder {
        private var entityType: Int = 0
        private var postId: String = ""
        private var commentId: String? = null
        private var tagId: String? = null
        private var reason: String? = null

        fun entityType(@DeleteType entityType: Int) = apply { this.entityType = entityType }
        fun postId(postId: String) = apply { this.postId = postId }
        fun commentId(commentId: String?) = apply { this.commentId = commentId }
        fun tagId(tagId: String?) = apply { this.tagId = tagId }
        fun reason(reason: String?) = apply { this.reason = reason }

        fun build() = DeleteExtras(
            entityType,
            postId,
            commentId,
            tagId,
            reason
        )
    }

    fun toBuilder(): Builder {
        return Builder().entityType(entityType)
            .tagId(tagId)
            .reason(reason)
            .postId(postId)
            .commentId(commentId)
    }
}