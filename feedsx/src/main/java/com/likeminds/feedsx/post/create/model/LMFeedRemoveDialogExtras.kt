package com.likeminds.feedsx.post.create.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedRemoveDialogExtras private constructor(
    val title: String,
    val description: String
) : Parcelable {
    class Builder {
        private var title: String = ""
        private var description: String = ""

        fun title(title: String) = apply { this.title = title }
        fun description(description: String) = apply { this.description = description }

        fun build() = LMFeedRemoveDialogExtras(title, description)
    }

    fun toBuilder(): Builder {
        return Builder().title(title).description(description)
    }
}