package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsx.media.model.MEDIA_ACTION_NONE
import com.likeminds.feedsx.media.model.MediaActions
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_ATTACHMENT
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS_ITEM
import kotlinx.parcelize.Parcelize

@Parcelize
class AttachmentViewData private constructor(
    @AttachmentType var attachmentType: Int,
    var attachmentMeta: AttachmentMetaViewData,
    var dynamicViewType: Int?,
    @MediaActions var mediaActions: String
) : Parcelable, BaseViewType {

    override val viewType: Int
        //TODO for multiple media
        get() = when {
            dynamicViewType != null -> {
                dynamicViewType!!
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
        private var mediaActions: String = MEDIA_ACTION_NONE

        fun attachmentType(@AttachmentType attachmentType: Int) =
            apply { this.attachmentType = attachmentType }

        fun attachmentMeta(attachmentMeta: AttachmentMetaViewData) =
            apply { this.attachmentMeta = attachmentMeta }

        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun mediaActions(@MediaActions mediaActions: String) =
            apply { this.mediaActions = mediaActions }

        fun build() = AttachmentViewData(
            attachmentType,
            attachmentMeta,
            dynamicViewType,
            mediaActions
        )
    }

    fun toBuilder(): Builder {
        return Builder().attachmentType(attachmentType)
            .attachmentMeta(attachmentMeta)
            .dynamicViewType(dynamicViewType)
            .mediaActions(mediaActions)
    }

}