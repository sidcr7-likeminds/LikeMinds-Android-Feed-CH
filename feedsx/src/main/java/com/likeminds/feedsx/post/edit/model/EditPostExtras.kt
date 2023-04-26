package com.likeminds.feedsx.post.edit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class EditPostExtras private constructor(
    val postId: String
) : Parcelable {
    class Builder {
        private var postId: String = ""

        fun postId(postId: String) = apply { this.postId = postId }

        fun build() = EditPostExtras(postId)
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
    }
}