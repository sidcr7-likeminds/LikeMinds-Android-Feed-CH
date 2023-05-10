package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_ATTACHMENT
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS_ITEM
import kotlinx.parcelize.Parcelize

@Parcelize
class AttachmentViewData private constructor(
    @AttachmentType var attachmentType: Int,
    val attachmentMeta: AttachmentMetaViewData,
    val dynamicViewType: Int?,
    val postId: String
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = when {
            dynamicViewType != null -> {
                dynamicViewType
            }
            attachmentType == DOCUMENT -> {
                ITEM_POST_DOCUMENTS_ITEM
            }
            else -> {
                ITEM_POST_ATTACHMENT
            }
        }

    class Builder {
        @AttachmentType
        private var attachmentType: Int = 0
        private var attachmentMeta: AttachmentMetaViewData =
            AttachmentMetaViewData.Builder().build()
        private var dynamicViewType: Int? = null
        private var postId: String = ""

        fun attachmentType(@AttachmentType attachmentType: Int) =
            apply { this.attachmentType = attachmentType }

        fun attachmentMeta(attachmentMeta: AttachmentMetaViewData) =
            apply { this.attachmentMeta = attachmentMeta }

        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun postId(postId: String) =
            apply { this.postId = postId }

        fun build() = AttachmentViewData(
            attachmentType,
            attachmentMeta,
            dynamicViewType,
            postId
        )
    }

    fun toBuilder(): Builder {
        return Builder().attachmentType(attachmentType)
            .attachmentMeta(attachmentMeta)
            .dynamicViewType(dynamicViewType)
            .postId(postId)
    }

}