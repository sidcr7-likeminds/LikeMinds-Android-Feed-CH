package com.likeminds.feedsx.post.create.model

import android.os.Parcelable
import com.likeminds.feedsx.media.model.SingleUriData
import kotlinx.parcelize.Parcelize

@Parcelize
class CreatePostResult private constructor(
    var text: String?,
    var attachments: ArrayList<SingleUriData>?
) : Parcelable {

    class Builder {
        private var text: String? = null
        private var attachments: ArrayList<SingleUriData>? = null

        fun text(text: String?) = apply { this.text = text }
        fun attachments(attachments: ArrayList<SingleUriData>?) =
            apply { this.attachments = attachments }

        fun build() = CreatePostResult(text, attachments)
    }

    fun toBuilder(): Builder {
        return Builder().text(text).attachments(attachments)
    }
}