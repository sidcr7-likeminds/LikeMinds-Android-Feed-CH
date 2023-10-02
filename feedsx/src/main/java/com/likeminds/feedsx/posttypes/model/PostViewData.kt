package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.utils.model.*
import com.likeminds.feedsx.widgets.model.WidgetViewData
import kotlinx.parcelize.Parcelize

@Parcelize
class PostViewData private constructor(
    val id: String,
    val text: String?,
    val alreadySeenFullContent: Boolean?,
    val isExpanded: Boolean,
    val attachments: List<AttachmentViewData>,
    val communityId: Int,
    val isPinned: Boolean,
    val isSaved: Boolean,
    val isLiked: Boolean,
    val isEdited: Boolean,
    val userId: String,
    val likesCount: Int,
    val commentsCount: Int,
    val menuItems: List<OverflowMenuItemViewData>,
    val replies: MutableList<CommentViewData>,
    val createdAt: Long,
    val updatedAt: Long,
    val user: UserViewData,
    val fromPostLiked: Boolean,
    val fromPostSaved: Boolean,
    val fromVideoAction: Boolean,
    val thumbnail: String?,
    val workerUUID: String,
    val isPosted: Boolean,
    val temporaryId: Long?,
    val uuid: String,
    val widget: WidgetViewData,
    val heading: String?,
    val onBehalfOfUUID: String?,
    val topics: List<LMFeedTopicViewData>
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = when {
            (attachments.size == 1 && attachments.first().attachmentType == IMAGE) -> {
                ITEM_POST_SINGLE_IMAGE
            }

            (attachments.size == 1 && attachments.first().attachmentType == VIDEO) -> {
                ITEM_POST_SINGLE_VIDEO
            }

            (attachments.isNotEmpty() && attachments.first().attachmentType == DOCUMENT) -> {
                ITEM_POST_DOCUMENTS
            }

            (attachments.size > 1 && (attachments.first().attachmentType == IMAGE || attachments.first().attachmentType == VIDEO)) -> {
                ITEM_POST_MULTIPLE_MEDIA
            }

            (attachments.size == 1 && attachments.first().attachmentType == LINK) -> {
                ITEM_POST_LINK
            }

            (attachments.size == 1 && attachments.first().attachmentType == ARTICLE) -> {
                ITEM_POST_ARTICLE
            }

            else -> {
                ITEM_POST_TEXT_ONLY
            }
        }

    class Builder {
        private var id: String = ""
        private var text: String? = null
        private var alreadySeenFullContent: Boolean? = null
        private var isExpanded: Boolean = false
        private var attachments: List<AttachmentViewData> = listOf()
        private var communityId: Int = 0
        private var isPinned: Boolean = false
        private var isSaved: Boolean = false
        private var isLiked: Boolean = false
        private var isEdited: Boolean = false
        private var userId: String = ""
        private var likesCount: Int = 0
        private var commentsCount: Int = 0
        private var menuItems: List<OverflowMenuItemViewData> = listOf()
        private var replies: MutableList<CommentViewData> = mutableListOf()
        private var createdAt: Long = 0
        private var updatedAt: Long = 0
        private var user: UserViewData = UserViewData.Builder().build()
        private var fromPostLiked: Boolean = false
        private var fromPostSaved: Boolean = false
        private var thumbnail: String? = null
        private var workerUUID: String = ""
        private var isPosted: Boolean = false
        private var temporaryId: Long? = null
        private var fromVideoAction: Boolean = false
        private var uuid: String = ""
        private var widget: WidgetViewData = WidgetViewData.Builder().build()
        private var heading: String? = null
        private var onBehalfOfUUID: String? = null
        private var topics: List<LMFeedTopicViewData> = emptyList()

        fun id(id: String) = apply { this.id = id }
        fun text(text: String?) = apply { this.text = text }

        fun alreadySeenFullContent(alreadySeenFullContent: Boolean?) =
            apply { this.alreadySeenFullContent = alreadySeenFullContent }

        fun isExpanded(isExpanded: Boolean) =
            apply { this.isExpanded = isExpanded }

        fun attachments(attachments: List<AttachmentViewData>) =
            apply { this.attachments = attachments }

        fun communityId(communityId: Int) = apply { this.communityId = communityId }
        fun isPinned(isPinned: Boolean) = apply { this.isPinned = isPinned }
        fun isSaved(isSaved: Boolean) = apply { this.isSaved = isSaved }
        fun isLiked(isLiked: Boolean) = apply { this.isLiked = isLiked }
        fun isEdited(isEdited: Boolean) = apply { this.isEdited = isEdited }
        fun userId(userId: String) = apply { this.userId = userId }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }
        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }
        fun menuItems(menuItems: List<OverflowMenuItemViewData>) =
            apply { this.menuItems = menuItems }

        fun replies(replies: MutableList<CommentViewData>) = apply { this.replies = replies }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun user(user: UserViewData) = apply { this.user = user }
        fun fromPostLiked(fromPostLiked: Boolean) = apply { this.fromPostLiked = fromPostLiked }
        fun fromPostSaved(fromPostSaved: Boolean) = apply { this.fromPostSaved = fromPostSaved }
        fun fromVideoAction(fromVideoAction: Boolean) =
            apply { this.fromVideoAction = fromVideoAction }

        fun thumbnail(thumbnail: String?) = apply { this.thumbnail = thumbnail }
        fun workerUUID(workerUUID: String) = apply { this.workerUUID = workerUUID }
        fun isPosted(isPosted: Boolean) = apply { this.isPosted = isPosted }
        fun temporaryId(temporaryId: Long?) = apply { this.temporaryId = temporaryId }
        fun uuid(uuid: String) = apply { this.uuid = uuid }
        fun widget(widget: WidgetViewData) = apply { this.widget = widget }
        fun heading(heading: String?) = apply { this.heading = heading }
        fun onBehalfOfUUID(onBehalfOfUUID: String?) = apply { this.onBehalfOfUUID = onBehalfOfUUID }
        fun topics(topics: List<LMFeedTopicViewData>) = apply { this.topics = topics }

        fun build() = PostViewData(
            id,
            text,
            alreadySeenFullContent,
            isExpanded,
            attachments,
            communityId,
            isPinned,
            isSaved,
            isLiked,
            isEdited,
            userId,
            likesCount,
            commentsCount,
            menuItems,
            replies,
            createdAt,
            updatedAt,
            user,
            fromPostLiked,
            fromPostSaved,
            fromVideoAction,
            thumbnail,
            workerUUID,
            isPosted,
            temporaryId,
            uuid,
            widget,
            heading,
            onBehalfOfUUID,
            topics
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .text(text)
            .alreadySeenFullContent(alreadySeenFullContent)
            .isExpanded(isExpanded)
            .attachments(attachments)
            .communityId(communityId)
            .isPinned(isPinned)
            .isSaved(isSaved)
            .isLiked(isLiked)
            .isEdited(isEdited)
            .userId(userId)
            .likesCount(likesCount)
            .commentsCount(commentsCount)
            .menuItems(menuItems)
            .replies(replies)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .user(user)
            .fromPostLiked(fromPostLiked)
            .fromPostSaved(fromPostSaved)
            .fromVideoAction(fromVideoAction)
            .thumbnail(thumbnail)
            .workerUUID(workerUUID)
            .isPosted(isPosted)
            .temporaryId(temporaryId)
            .uuid(uuid)
            .widget(widget)
            .heading(heading)
            .onBehalfOfUUID(onBehalfOfUUID)
            .topics(topics)
    }
}