package com.likeminds.feedsx.post.create.model

import android.os.Parcelable
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.posttypes.model.*
import kotlinx.parcelize.Parcelize

@Parcelize
class CreatePostExtras private constructor(
    @AttachmentType
    val attachmentType: Int,
    val source: String?,
    val linkOGTagsViewData: LinkOGTagsViewData?,
    val attachmentUri: SingleUriData?
) : Parcelable {
    class Builder {
        private var attachmentType: Int = VIDEO
        private var source: String? = null
        private var linkOGTagsViewData: LinkOGTagsViewData? = null
        private var attachmentUri: SingleUriData? = null

        fun attachmentType(@AttachmentType attachmentType: Int) =
            apply { this.attachmentType = attachmentType }

        fun source(source: String?) = apply { this.source = source }
        fun linkOGTagsViewData(linkOGTagsViewData: LinkOGTagsViewData?) =
            apply { this.linkOGTagsViewData = linkOGTagsViewData }

        fun attachmentUri(attachmentUri: SingleUriData?) =
            apply { this.attachmentUri = attachmentUri }

        fun build() = CreatePostExtras(
            attachmentType,
            source,
            linkOGTagsViewData,
            attachmentUri
        )
    }

    fun toBuilder(): Builder {
        return Builder().attachmentType(attachmentType)
            .source(source)
            .linkOGTagsViewData(linkOGTagsViewData)
            .attachmentUri(attachmentUri)
    }
}