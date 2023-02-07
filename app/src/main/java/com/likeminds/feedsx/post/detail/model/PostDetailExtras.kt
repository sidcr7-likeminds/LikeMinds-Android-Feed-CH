package com.likeminds.feedsx.post.detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PostDetailExtras private constructor(
    var postId: String,
    var isEditTextFocused: Boolean,
    var commentsCount: Int
) : Parcelable {

    class Builder {

        private var postId: String = ""
        private var isEditTextFocused: Boolean = false
        private var commentsCount: Int = 0

        fun postId(postId: String) = apply { this.postId = postId }
        fun isEditTextFocused(isEditTextFocused: Boolean) =
            apply { this.isEditTextFocused = isEditTextFocused }

        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }

        fun build() = PostDetailExtras(
            postId,
            isEditTextFocused,
            commentsCount
        )
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
            .isEditTextFocused(isEditTextFocused)
            .commentsCount(commentsCount)
    }
}