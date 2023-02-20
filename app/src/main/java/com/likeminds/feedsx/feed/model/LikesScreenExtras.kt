package com.likeminds.feedsx.feed.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LikesScreenExtras private constructor(
    var postId: String,
    var likesCount: Int
) : Parcelable {

    class Builder {

        private var postId: String = ""
        private var likesCount: Int = 0

        fun postId(postId: String) = apply { this.postId = postId }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }

        fun build() = LikesScreenExtras(postId, likesCount)
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
            .likesCount(likesCount)
    }
}