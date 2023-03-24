package com.likeminds.feedsx.post.create.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CreatePostResult private constructor(
    var showUploader: Boolean
) : Parcelable {
    class Builder {
        private var showUploader: Boolean = false

        fun showUploader(showUploader: Boolean) = apply { this.showUploader = showUploader }

        fun build() = CreatePostResult(showUploader)
    }

    fun toBuilder(): Builder {
        return Builder().showUploader(showUploader)
    }
}