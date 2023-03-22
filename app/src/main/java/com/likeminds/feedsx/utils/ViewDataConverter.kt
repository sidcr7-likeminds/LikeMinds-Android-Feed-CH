package com.likeminds.feedsx.utils

import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.PDF
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.posttypes.model.AttachmentMetaViewData
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.LinkOGTags
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO
import com.likeminds.likemindsfeed.post.model.Like
import com.likeminds.likemindsfeed.sdk.model.User

object ViewDataConverter {

    /**--------------------------------
     * Media Model -> View Data Model
    --------------------------------*/

    // Converts the SingleUriData (contains the data of media) to AttachmentViewData
    fun convertSingleDataUri(singleUriData: SingleUriData): AttachmentViewData {
        val attachmentType: Int?
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

    // converts internal media filetype to FileType
    fun convertFileType(fileType: String): Int {
        return when (fileType) {
            IMAGE -> com.likeminds.feedsx.utils.mediauploader.model.IMAGE
            PDF -> com.likeminds.feedsx.utils.mediauploader.model.PDF
            VIDEO -> com.likeminds.feedsx.utils.mediauploader.model.VIDEO
            else -> com.likeminds.feedsx.utils.mediauploader.model.IMAGE
        }
    }

    /**--------------------------------
     * Network Model -> View Data Model
    --------------------------------*/

    // converts User network model to view data model
    fun convertUser(
        user: User?
    ): UserViewData {
        if (user == null) {
            return UserViewData.Builder().build()
        }
        return UserViewData.Builder()
            .id(user.id)
            .name(user.name)
            .imageUrl(user.imageUrl)
            .userUniqueId(user.userUniqueId)
            .customTitle(user.customTitle)
            .isGuest(user.isGuest)
            .isDeleted(user.isDeleted)
            .build()
    }

    // converts Like network model to view data model
    fun convertLikes(
        like: Like,
        users: Map<String, User>
    ): LikeViewData {
        return LikeViewData.Builder()
            .id(like.id)
            .userId(like.userId)
            .createdAt(like.createdAt)
            .updatedAt(like.updatedAt)
            .user(convertUser(users[like.userId]))
            .build()
    }

    // converts LinkOGTags network model to view data model
    fun convertOGTags(
        linkOGTags: com.likeminds.likemindsfeed.post.model.LinkOGTags
    ): LinkOGTags {
        return LinkOGTags.Builder()
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .description(linkOGTags.description)
            .url(linkOGTags.url)
            .build()
    }
}