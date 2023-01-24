package com.likeminds.feedsx.post.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_ATTACHMENT
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS_ITEM
import kotlinx.parcelize.Parcelize

@Parcelize
class AttachmentViewData private constructor(
    @AttachmentType var fileType: Int,
    var fileUrl: String,
    var fileFormat: String?,
    var fileSize: String
) : Parcelable, BaseViewType {

    override val viewType: Int
    //TODO for multiple media
        get() = when {
            (fileType == DOCUMENT) -> {
                ITEM_POST_DOCUMENTS_ITEM
            }
            else -> {
                ITEM_POST_ATTACHMENT
            }
        }

    class Builder {
        @AttachmentType
        private var fileType: Int = 0
        private var fileUrl: String = ""
        private var fileFormat: String? = null
        private var fileSize: String = ""

        fun fileType(@AttachmentType fileType: Int) = apply { this.fileType = fileType }
        fun fileUrl(fileUrl: String) = apply { this.fileUrl = fileUrl }
        fun fileFormat(fileFormat: String?) = apply { this.fileFormat = fileFormat }
        fun fileSize(fileSize: String) = apply { this.fileSize = fileSize }

        fun build() = AttachmentViewData(
            fileType,
            fileUrl,
            fileFormat,
            fileSize
        )
    }

    fun toBuilder(): Builder {
        return Builder().fileType(fileType)
            .fileUrl(fileUrl)
            .fileFormat(fileFormat)
            .fileSize(fileSize)
    }

}