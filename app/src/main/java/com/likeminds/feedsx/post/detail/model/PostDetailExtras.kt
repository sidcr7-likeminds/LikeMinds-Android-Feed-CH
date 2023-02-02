package com.likeminds.feedsx.post.detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PostDetailExtras private constructor(
    var commentsCount: Int
) : Parcelable {

    class Builder {

        private var commentsCount: Int = 0

        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }

        fun build() = PostDetailExtras(
            commentsCount
        )
    }

    fun toBuilder(): Builder {
        return Builder().commentsCount(commentsCount)
    }
}