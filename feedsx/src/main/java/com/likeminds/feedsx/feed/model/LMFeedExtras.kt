package com.likeminds.feedsx.feed.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedExtras private constructor(
    val apiKey: String,
    val userName: String,
    val uuid: String,
    val isGuest: Boolean
) : Parcelable {

    class Builder {

        private var apiKey: String = ""
        private var userName: String = ""
        private var uuid: String = ""
        private var isGuest: Boolean = false

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun userName(userName: String) = apply { this.userName = userName }
        fun uuid(uuid: String) = apply { this.uuid = uuid }
        fun isGuest(isGuest: Boolean) = apply { this.isGuest = isGuest }

        fun build() = LMFeedExtras(
            apiKey,
            userName,
            uuid,
            isGuest
        )
    }

    fun toBuilder(): Builder {
        return Builder().apiKey(apiKey)
            .userName(userName)
            .uuid(uuid)
            .isGuest(isGuest)
    }
}