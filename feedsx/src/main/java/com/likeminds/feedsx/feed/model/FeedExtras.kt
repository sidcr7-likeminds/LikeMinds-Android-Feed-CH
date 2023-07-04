package com.likeminds.feedsx.feed.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FeedExtras private constructor(
    val apiKey: String,
    val userName: String,
    val userId: String,
    val isGuest: Boolean
) : Parcelable {

    class Builder {

        private var apiKey: String = ""
        private var userName: String = ""
        private var userId: String = ""
        private var isGuest: Boolean = false

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun userName(userName: String) = apply { this.userName = userName }
        fun userId(userId: String) = apply { this.userId = userId }
        fun isGuest(isGuest: Boolean) = apply { this.isGuest = isGuest }

        fun build() = FeedExtras(
            apiKey,
            userName,
            userId,
            isGuest
        )
    }

    fun toBuilder(): Builder {
        return Builder().apiKey(apiKey)
            .userName(userName)
            .userId(userId)
            .isGuest(isGuest)
    }
}