package com.likeminds.feedsx.post.detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PostDetailExtras private constructor(
    var postId: String,
    var commentId: String?,
    var isEditTextFocused: Boolean
) : Parcelable {

    class Builder {

        private var postId: String = ""
        private var isEditTextFocused: Boolean = false
        private var commentId: String? = null

        fun postId(postId: String) = apply { this.postId = postId }
        fun isEditTextFocused(isEditTextFocused: Boolean) =
            apply { this.isEditTextFocused = isEditTextFocused }


        fun commentId(commentId: String?) = apply { this.commentId = commentId }

        fun build() = PostDetailExtras(
            postId,
            commentId,
            isEditTextFocused
        )
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
            .isEditTextFocused(isEditTextFocused)
            .commentId(commentId)
    }
}