package com.likeminds.feedsx.delete.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DeleteExtras private constructor(
    @DeleteType
    var entityType: Int,
    var postId: String,
    var commentId: String?,
) : Parcelable {

    class Builder {
        private var entityType: Int = 0
        private var postId: String = ""
        private var commentId: String? = null

        fun entityType(@DeleteType entityType: Int) = apply { this.entityType = entityType }
        fun postId(postId: String) = apply { this.postId = postId }

        fun commentId(commentId: String?) = apply { this.commentId = commentId }

        fun build() = DeleteExtras(entityType, postId, commentId)
    }

    fun toBuilder(): Builder {
        return Builder().entityType(entityType)
            .postId(postId)
            .commentId(commentId)
    }
}