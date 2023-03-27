package com.likeminds.feedsx.utils

import com.likeminds.feedsx.db.models.UserEntity
import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.posttypes.model.AttachmentMetaViewData
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.report.model.ReportTagViewData
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO
import com.likeminds.likemindsfeed.moderation.model.ReportTag
import com.likeminds.likemindsfeed.post.model.Like
import com.likeminds.likemindsfeed.sdk.model.User

object ViewDataConverter {

    /**--------------------------------
     * Media Model -> View Data Model
    --------------------------------*/

    // Converts the SingleDataUri (contains the data of media) to AttachmentViewData
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
        likes: List<Like>,
        users: Map<String, User>
    ): List<LikeViewData> {
        return likes.map { like ->
            //get user id
            val likedById = like.userId

            //get user
            val likedBy = users[likedById]

            //convert view data
            val likedByViewData = if (likedBy == null) {
                //todo create deleted user
                UserViewData.Builder().build()
            } else {
                convertUser(likedBy)
            }

            //create likeview data
            LikeViewData.Builder()
                .id(like.id)
                .userId(like.userId)
                .createdAt(like.createdAt)
                .updatedAt(like.updatedAt)
                .user(likedByViewData)
                .build()
        }
    }

    /**--------------------------------
     * Network Model -> Db Model
    --------------------------------*/
    fun convertUserEntity(user: User): UserEntity {
        return UserEntity.Builder()
            .id(user.id)
            .imageUrl(user.imageUrl)
            .isGuest(user.isGuest)
            .name(user.name)
            .updatedAt(user.updatedAt)
            .customTitle(user.customTitle)
            .isDeleted(user.isDeleted)
            .userUniqueId(user.userUniqueId)
            .build()
    }

    fun convertReportTag(
        reportTag: ReportTag
    ): ReportTagViewData {
        return ReportTagViewData.Builder()
            .id(reportTag.id)
            .name(reportTag.name)
            .isSelected(false)
            .build()
    }
}