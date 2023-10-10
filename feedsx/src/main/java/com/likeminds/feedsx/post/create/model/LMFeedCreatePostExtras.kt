package com.likeminds.feedsx.post.create.model

import android.os.Parcelable
import com.likeminds.feedsx.posttypes.model.*
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedCreatePostExtras private constructor(
    @AttachmentType
    val attachmentType: Int,
    val source: String?,
    val linkOGTagsViewData: LinkOGTagsViewData?,
    val isAdmin: Boolean
) : Parcelable {
    class Builder {
        private var attachmentType: Int = VIDEO
        private var source: String? = null
        private var linkOGTagsViewData: LinkOGTagsViewData? = null
        private var isAdmin: Boolean = false

        fun attachmentType(@AttachmentType attachmentType: Int) =
            apply { this.attachmentType = attachmentType }

        fun source(source: String?) = apply { this.source = source }
        fun linkOGTagsViewData(linkOGTagsViewData: LinkOGTagsViewData?) =
            apply { this.linkOGTagsViewData = linkOGTagsViewData }

        fun isAdmin(isAdmin: Boolean) = apply { this.isAdmin = isAdmin }

        fun build() = LMFeedCreatePostExtras(
            attachmentType,
            source,
            linkOGTagsViewData,
            isAdmin
        )
    }

    fun toBuilder(): Builder {
        return Builder().attachmentType(attachmentType)
            .source(source)
            .linkOGTagsViewData(linkOGTagsViewData)
            .isAdmin(isAdmin)
    }
}