package com.likeminds.feedsx.likes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LikesScreenExtras private constructor(
    @LikesScreenEntityType
    val entityType: Int,
    val postId: String,
    val commentId: String?,
) : Parcelable {

    class Builder {

        @LikesScreenEntityType
        private var entityType: Int = 0
        private var postId: String = ""
        private var commentId: String? = null

        fun entityType(@LikesScreenEntityType entityType: Int) =
            apply { this.entityType = entityType }

        fun postId(postId: String) = apply { this.postId = postId }
        fun commentId(commentId: String?) = apply { this.commentId = commentId }

        fun build() = LikesScreenExtras(
            entityType,
            postId,
            commentId
        )
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
            .entityType(entityType)
            .commentId(commentId)
    }
}