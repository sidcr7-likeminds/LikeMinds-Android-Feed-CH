package com.likeminds.feedsx.utils

import android.net.Uri
import android.util.Base64
import com.likeminds.feedsx.db.models.*
import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.PDF
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.utils.mediauploader.utils.AWSKeys
import com.likeminds.feedsx.utils.membertagging.model.MemberTagViewData
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO
import com.likeminds.likemindsfeed.helper.model.TagMember
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
        return AttachmentViewData.Builder()
            .dynamicViewType(viewType)
            .attachmentType(attachmentType)
            .attachmentMeta(
                AttachmentMetaViewData.Builder()
                    .name(singleUriData.mediaName)
                    .uri(singleUriData.uri)
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

    // converts Like network model to view data model
    fun convertLikes(
        like: Like,
        users: Map<String, User>
    ): LikeViewData {
        val likeCreator = like.userId
        val user = users[likeCreator]
        val userViewData = if (user == null) {
            createDeletedUser()
        } else {
            convertUser(user)
        }

        return LikeViewData.Builder()
            .id(like.id)
            .userId(like.userId)
            .createdAt(like.createdAt)
            .updatedAt(like.updatedAt)
            .user(userViewData)
            .build()
    }

    fun convertPost(
        post: Post,
        users: Map<String, User>
    ): PostViewData {
        val postCreator = post.userId
        val user = users[postCreator]
        val postId = post.id
        val userViewData = if (user == null) {
            createDeletedUser()
        } else {
            convertUser(user)
        }

        return PostViewData.Builder()
            .id(post.id)
            .text(post.text)
            .communityId(post.communityId)
            .isPinned(post.isPinned)
            .isSaved(post.isSaved)
            .isLiked(post.isLiked)
            .menuItems(convertOverflowMenuItems(post.menuItems, postId))
            .attachments(convertAttachments(post.attachments))
            .userId(postCreator)
            .likesCount(post.likesCount)
            .commentsCount(post.commentsCount)
            .createdAt(post.createdAt)
            .updatedAt(post.updatedAt)
            .user(userViewData)
            .build()
    }

    private fun convertOverflowMenuItems(
        menuItems: List<MenuItem>,
        entityId: String
    ): List<OverflowMenuItemViewData> {
        return menuItems.map { menuItem ->
            OverflowMenuItemViewData.Builder()
                .title(menuItem.title)
                .entityId(entityId)
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
    private fun convertAttachmentMeta(attachmentMeta: AttachmentMeta?): AttachmentMetaViewData {
        if (attachmentMeta == null) {
            return AttachmentMetaViewData.Builder().build()
        }
        return AttachmentMetaViewData.Builder()
            .name(attachmentMeta.name)
            .url(attachmentMeta.url)
            .format(attachmentMeta.format)
            .size(attachmentMeta.size)
            .duration(attachmentMeta.duration)
            .pageCount(attachmentMeta.pageCount)
            .ogTags(convertLinkOGTags(attachmentMeta.ogTags))
            .width(attachmentMeta.width)
            .height(attachmentMeta.height)
            .build()
    }

    /**
     * convert [LinkOGTagsViewData] to [LinkOGTagsViewData]
     * @param linkOGTags: object of [LinkOGTagsViewData]
     **/
    fun convertLinkOGTags(linkOGTags: LinkOGTags): LinkOGTagsViewData {
        return LinkOGTagsViewData.Builder()
            .url(linkOGTags.url)
            .description(linkOGTags.description)
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .build()
    }

    fun convertMemberTag(tagMember: TagMember): MemberTagViewData {
        val nameDrawable = MemberImageUtil.getNameDrawable(
            MemberImageUtil.SIXTY_PX,
            tagMember.id.toString(),
            tagMember.name
        )
        return MemberTagViewData.Builder()
            .id(tagMember.id)
            .imageUrl(tagMember.imageUrl)
            .isGuest(tagMember.isGuest)
            .name(tagMember.name)
            .userUniqueId(tagMember.userUniqueId)
            .placeHolder(nameDrawable.first)
            .build()
    }

    /**--------------------------------
     * View Data -> Db Model
    --------------------------------*/

    fun convertPost(
        temporaryId: Long,
        uuid: String,
        thumbnail: String,
        text: String?
    ): PostEntity {
        return PostEntity.Builder()
            .id(temporaryId)
            .uuid(uuid)
            .thumbnail(thumbnail)
            .text(text)
            .build()
    }

    fun convertAttachment(
        postId: Long,
        singleUriData: SingleUriData
    ): AttachmentEntity {
        val attachmentType = when (singleUriData.fileType) {
            IMAGE -> {
                com.likeminds.feedsx.posttypes.model.IMAGE
            }
            VIDEO -> {
                com.likeminds.feedsx.posttypes.model.VIDEO
            }
            else -> {
                com.likeminds.feedsx.posttypes.model.DOCUMENT
            }
        }
        return AttachmentEntity.Builder()
            .postId(postId)
            .attachmentType(attachmentType)
            .attachmentMeta(convertAttachmentMeta(singleUriData))
            .build()
    }

    private fun convertAttachmentMeta(
        singleUriData: SingleUriData
    ): AttachmentMetaEntity {
        val url = String(
            Base64.decode(
                AWSKeys.getBucketBaseUrl(),
                Base64.DEFAULT
            )
        ) + singleUriData.awsFolderPath
        return AttachmentMetaEntity.Builder().name(singleUriData.mediaName)
            .url(url)
            .uri(singleUriData.uri.toString())
            .pageCount(singleUriData.pdfPageCount)
            .size(singleUriData.size)
            .duration(singleUriData.duration)
            .format(singleUriData.format)
            .awsFolderPath(singleUriData.awsFolderPath)
            .localFilePath(singleUriData.localFilePath)
            .width(singleUriData.width)
            .height(singleUriData.height)
            .build()
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