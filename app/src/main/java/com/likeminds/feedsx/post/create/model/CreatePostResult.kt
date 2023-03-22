package com.likeminds.feedsx.post.create.model

import android.os.Parcelable
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.posttypes.model.LinkOGTags
import kotlinx.parcelize.Parcelize

@Parcelize
class CreatePostResult private constructor(
    var text: String?,
    var attachments: ArrayList<SingleUriData>?,
    var ogTags: LinkOGTags?
) : Parcelable {

    class Builder {
        private var text: String? = null
        private var attachments: ArrayList<SingleUriData>? = null
        private var ogTags: LinkOGTags? = null

        fun text(text: String?) = apply { this.text = text }
        fun attachments(attachments: ArrayList<SingleUriData>?) =
            apply { this.attachments = attachments }

        fun ogTags(ogTags: LinkOGTags?) = apply { this.ogTags = ogTags }

        fun build() = CreatePostResult(
            text,
            attachments,
            ogTags
        )
    }

    fun toBuilder(): Builder {
        return Builder().text(text)
            .ogTags(ogTags)
            .attachments(attachments)
    }
}