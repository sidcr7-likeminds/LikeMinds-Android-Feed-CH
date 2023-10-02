package com.likeminds.feedsx.post.edit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedEditPostExtras private constructor(
    val postId: String,
    val viewType: Int?,
) : Parcelable {
    class Builder {
        private var postId: String = ""
        private var viewType: Int? = null

        fun postId(postId: String) = apply { this.postId = postId }
        fun viewType(viewType: Int?) = apply { this.viewType = viewType }

        fun build() = LMFeedEditPostExtras(postId, viewType)
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId).viewType(viewType)
    }
}