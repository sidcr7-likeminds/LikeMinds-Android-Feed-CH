package com.likeminds.feedsx.utils

import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.posttypes.model.AttachmentMetaViewData
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO

object ViewDataConverter {

    fun convertSingleDataUri(singleUriData: SingleUriData): AttachmentViewData {
        var attachmentType: Int? = null
        val viewType = when (singleUriData.fileType) {
            IMAGE -> {
                attachmentType = com.likeminds.feedsx.posttypes.model.IMAGE
                ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE
            }
            VIDEO -> {
                attachmentType = com.likeminds.feedsx.posttypes.model.VIDEO
                ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO
            }
            else -> {
                attachmentType = com.likeminds.feedsx.posttypes.model.DOCUMENT
                ITEM_CREATE_POST_DOCUMENTS_ITEM
            }
        }
        return AttachmentViewData.Builder()
            .dynamicViewType(viewType)
            .attachmentType(attachmentType)
            .attachmentMeta(
                AttachmentMetaViewData.Builder()
                    .name(singleUriData.mediaName)
                    .uri(singleUriData.uri)
                    .duration(singleUriData.duration.toString())
                    .pageCount(singleUriData.pdfPageCount)
                    .size(MediaUtils.getFileSizeText(singleUriData.size))
                    .build()
            )
            .build()
    }
}