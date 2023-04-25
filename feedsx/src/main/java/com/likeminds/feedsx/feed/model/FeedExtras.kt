package com.likeminds.feedsx.feed.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FeedExtras private constructor(
    val apiKey: String,
    val userName: String,
    val userId: String,
    val isLoggedIn: Boolean
) : Parcelable {

    class Builder {

        private var apiKey: String = ""
        private var userName: String = ""
        private var userId: String = ""
        private var isLoggedIn: Boolean = false

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun userName(userName: String) = apply { this.userName = userName }
        fun userId(userId: String) = apply { this.userId = userId }
        fun isLoggedIn(isLoggedIn: Boolean) = apply { this.isLoggedIn = isLoggedIn }

        fun build() = FeedExtras(
            apiKey,
            userName,
            userId,
            isLoggedIn
        )
    }

    fun toBuilder(): Builder {
        return Builder().apiKey(apiKey)
            .userName(userName)
            .userId(userId)
            .isLoggedIn(isLoggedIn)
    }
}