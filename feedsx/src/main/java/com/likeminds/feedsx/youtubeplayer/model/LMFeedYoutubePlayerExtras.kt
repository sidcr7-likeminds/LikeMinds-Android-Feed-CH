package com.likeminds.feedsx.youtubeplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedYoutubePlayerExtras private constructor(
    val videoId: String
) : Parcelable {
    class Builder {
        private var videoId: String = ""

        fun videoId(videoId: String) = apply { this.videoId = videoId }

        fun build() = LMFeedYoutubePlayerExtras(videoId)
    }

    fun toBuilder(): Builder {
        return Builder().videoId(videoId)
    }
}