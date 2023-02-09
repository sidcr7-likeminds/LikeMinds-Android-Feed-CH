package com.likeminds.feedsx.post.detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PostDetailExtras private constructor(
    var postId: String,
    var isEditTextFocused: Boolean,
    var commentsCount: Int,
    var commentId: String?
) : Parcelable {

    class Builder {

        private var postId: String = ""
        private var isEditTextFocused: Boolean = false
        private var commentsCount: Int = 0
        private var commentId: String? = null

        fun postId(postId: String) = apply { this.postId = postId }
        fun isEditTextFocused(isEditTextFocused: Boolean) =
            apply { this.isEditTextFocused = isEditTextFocused }

        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }

        fun commentId(commentId: String?) = apply { this.commentId = commentId }

        fun build() = PostDetailExtras(
            postId,
            isEditTextFocused,
            commentsCount,
            commentId
        )
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
            .isEditTextFocused(isEditTextFocused)
            .commentsCount(commentsCount)
            .commentId(commentId)
    }
}