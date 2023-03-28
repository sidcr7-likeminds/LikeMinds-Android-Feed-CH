package com.likeminds.feedsx.utils

import android.net.Uri
import android.util.Base64
import android.util.Log
import com.likeminds.feedsx.db.models.*
import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.db.models.UserEntity
import com.likeminds.feedsx.delete.model.ReasonChooseViewData
import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.PDF
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.utils.mediauploader.utils.AWSKeys
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.report.model.ReportTagViewData
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO
import com.likeminds.likemindsfeed.moderation.model.ReportTag
import com.likeminds.likemindsfeed.post.model.*
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
        Log.d("PUI", "convertSingleDataUri: ${singleUriData.pdfPageCount}")
        return AttachmentViewData.Builder()
            .dynamicViewType(viewType)
            .attachmentType(attachmentType)
            .attachmentMeta(
                AttachmentMetaViewData.Builder()
                    .name(singleUriData.mediaName)
                    .duration(singleUriData.duration)
                    .format(singleUriData.format)
                    .pageCount(singleUriData.pdfPageCount)
                    .width(singleUriData.width)
                    .height(singleUriData.height)
                    .size(singleUriData.size)
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
     * View Data Model -> Network Model
    --------------------------------*/

    fun createAttachments(
        attachments: List<AttachmentViewData>
    ): List<Attachment> {
        return attachments.map {
            convertAttachment(it)
        }
    }

    fun convertAttachment(
        attachment: AttachmentViewData
    ): Attachment {
        return Attachment.Builder()
            .attachmentType(attachment.attachmentType)
            .attachmentMeta(convertAttachmentMeta(attachment.attachmentMeta))
            .build()
    }

    private fun convertAttachmentMeta(
        attachmentMeta: AttachmentMetaViewData
    ): AttachmentMeta {
        Log.d("PUI", "convertAttachmentMeta-1: ${attachmentMeta.pageCount}")
        return AttachmentMeta.Builder().name(attachmentMeta.name)
            .ogTags(convertOGTags(attachmentMeta.ogTags))
            .url(attachmentMeta.url)
            .size(attachmentMeta.size)
            .duration(attachmentMeta.duration)
            .pageCount(attachmentMeta.pageCount)
            .format(attachmentMeta.format)
            .height(attachmentMeta.height)
            .width(attachmentMeta.width)
            .build()
    }

    // creates attachment list of Network Model for link attachment
    fun convertAttachments(
        linkOGTagsViewData: LinkOGTagsViewData
    ): List<Attachment> {
        return listOf(
            Attachment.Builder()
                .attachmentType(LINK)
                .attachmentMeta(convertAttachmentMeta(linkOGTagsViewData))
                .build()
        )
    }

    // creates AttachmentMeta Network Model for link attachment meta
    private fun convertAttachmentMeta(
        linkOGTagsViewData: LinkOGTagsViewData
    ): AttachmentMeta {
        return AttachmentMeta.Builder()
            .ogTags(convertOGTags(linkOGTagsViewData))
            .build()
    }

    // converts LinkOGTags view data model to network model
    private fun convertOGTags(
        linkOGTagsViewData: LinkOGTagsViewData
    ): LinkOGTags {
        return LinkOGTags.Builder()
            .title(linkOGTagsViewData.title)
            .image(linkOGTagsViewData.image)
            .description(linkOGTagsViewData.description)
            .url(linkOGTagsViewData.url)
            .build()
    }

    /**--------------------------------
     * Network Model -> View Data Model
    --------------------------------*/

    // converts User network model to view data model
    fun convertUser(
        user: User?
    ): UserViewData {
        if (user == null) return UserViewData.Builder().build()
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

    //created a deleted user object
    private fun createDeletedUser(): UserViewData {
        val tempUserId = (System.currentTimeMillis() / 1000).toInt()
        return UserViewData.Builder()
            .id(tempUserId)
            .name("Deleted User")
            .imageUrl("")
            .userUniqueId("$tempUserId")
            .customTitle(null)
            .isGuest(false)
            .isDeleted(true)
            .build()
    }

    /**
     * convert list of [Post] and usersMap [Map] of String to User
     * to [PostViewData]
     *
     * @param posts: list of [Post]
     * @param usersMap: [Map] of String to User
     * */
    fun convertUniversalFeedPosts(
        posts: List<Post>,
        usersMap: Map<String, User>
    ): List<PostViewData> {
        return posts.map { post ->
            val postCreator = post.userId
            val user = usersMap[postCreator]
            val postId = post.id

            val userViewData = if (user == null) {
                createDeletedUser()
            } else {
                convertUser(user)
            }

            PostViewData.Builder()
                .id(postId)
                .text(post.text)
                .communityId(post.communityId)
                .isPinned(post.isPinned)
                .isSaved(post.isSaved)
                .isLiked(post.isLiked)
                .menuItems(convertOverflowMenuItems(post.menuItems))
                .attachments(convertAttachments(post.attachments))
                .userId(postCreator)
                .likesCount(post.likesCount)
                .commentsCount(post.commentsCount)
                .createdAt(post.createdAt)
                .updatedAt(post.updatedAt)
                .user(userViewData)
                .build()
        }
    }

    /**
     * convert list of [MenuItem] to [OverflowMenuItemViewData]
     * @param menuItems: list of [MenuItem]
     * */
    private fun convertOverflowMenuItems(
        menuItems: List<MenuItem>
    ): List<OverflowMenuItemViewData> {
        return menuItems.map { menuItem ->
            OverflowMenuItemViewData.Builder()
                .title(menuItem.title)
                .build()
        }
    }

    /**
     * convert list of [Attachment] to list of [AttachmentViewData]
     * @param attachments: list of [Attachment]
     **/
    private fun convertAttachments(attachments: List<Attachment>?): List<AttachmentViewData> {
        if (attachments == null) return emptyList()
        return attachments.map { attachment ->
            AttachmentViewData.Builder()
                .attachmentType(attachment.attachmentType)
                .attachmentMeta(convertAttachmentMeta(attachment.attachmentMeta))
                .build()
        }
    }

    /**
     * convert [AttachmentMeta] to [AttachmentMetaViewData]
     * @param attachmentMeta: object of [AttachmentMeta]
     **/
    private fun convertAttachmentMeta(attachmentMeta: AttachmentMeta): AttachmentMetaViewData {
        return AttachmentMetaViewData.Builder()
            .name(attachmentMeta.name)
            .url(attachmentMeta.url)
            .format(attachmentMeta.format)
            .size(MediaUtils.getFileSizeText(attachmentMeta.size ?: 0L))
            .duration(MediaUtils.formatSeconds(attachmentMeta.duration ?: 0))
            .pageCount(attachmentMeta.pageCount)
            .ogTags(convertLinkOGTags(attachmentMeta.ogTags))
            .width(attachmentMeta.width)
            .height(attachmentMeta.height)
            .build()
    }

    /**
     * convert [LinkOGTags] to [LinkOGTagsViewData]
     * @param linkOGTags: object of [LinkOGTags]
     **/
    private fun convertLinkOGTags(linkOGTags: LinkOGTags): LinkOGTagsViewData {
        return LinkOGTagsViewData.Builder()
            .url(linkOGTags.url)
            .description(linkOGTags.description)
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .build()
    }

    /**
     * convert list of [Like] to list of [LikeViewData]
     * @param likes: list of [Like]
     * @param users: [Map] of String to User
     * */
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
                createDeletedUser()
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

    /**
     * convert list of [ReportTag] to list of [ReportTagViewData]
     * @param tags: list of [ReportTag]
     * */
    fun convertReportTag(
        tags: List<ReportTag>
    ): List<ReportTagViewData> {
        return tags.map { tag ->
            ReportTagViewData.Builder()
                .id(tag.id)
                .name(tag.name)
                .isSelected(false)
                .build()
        }
    }

    /**
     * convert list of [ReportTag] to list of [ReasonChooseViewData]
     * @param tags: list of [ReportTag]
     * */
    fun convertDeleteTag(
        tags: List<ReportTag>
    ): MutableList<ReasonChooseViewData> {
        return tags.map { tag ->
            ReasonChooseViewData.Builder()
                .value(tag.name)
                .build()
        }.toMutableList()
    }

    /**--------------------------------
     * Network Model -> Db Model
    --------------------------------*/
    fun createUserEntity(user: User): UserEntity {
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

    /**--------------------------------
     * Db Model -> View Data Model
    --------------------------------*/

    fun convertUser(user: UserEntity): UserViewData {
        return UserViewData.Builder()
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

    fun convertPost(postWithAttachments: PostWithAttachments): PostViewData {
        val post = postWithAttachments.post
        val attachments = postWithAttachments.attachments
        return PostViewData.Builder()
            .temporaryId(post.id)
            .thumbnail(post.thumbnail)
            .uuid(post.uuid)
            .isPosted(post.isPosted)
            .attachments(convertAttachmentsEntity(attachments))
            .build()
    }

    private fun convertAttachmentsEntity(attachments: List<AttachmentEntity>): List<AttachmentViewData> {
        return attachments.map { attachment ->
            convertAttachment(attachment)
        }
    }

    fun convertAttachment(attachment: AttachmentEntity): AttachmentViewData {
        return AttachmentViewData.Builder()
            .attachmentType(attachment.attachmentType)
            .attachmentMeta(convertAttachmentMeta(attachment.attachmentMeta))
            .build()
    }

    private fun convertAttachmentMeta(attachmentMeta: AttachmentMetaEntity): AttachmentMetaViewData {
        return AttachmentMetaViewData.Builder()
            .url(attachmentMeta.url)
            .name(attachmentMeta.name)
            .size(attachmentMeta.size)
            .duration(attachmentMeta.duration)
            .format(attachmentMeta.format)
            .pageCount(attachmentMeta.pageCount)
            .uri(Uri.parse(attachmentMeta.uri))
            .width(attachmentMeta.width)
            .height(attachmentMeta.height)
            .build()
    }
}