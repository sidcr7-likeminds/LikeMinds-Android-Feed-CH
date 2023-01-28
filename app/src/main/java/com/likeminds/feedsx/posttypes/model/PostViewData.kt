package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.utils.SeeMoreUtil
import com.likeminds.feedsx.utils.model.*
import kotlinx.parcelize.Parcelize

@Parcelize
class PostViewData private constructor(
    var id: String,
    var text: String,
    var shortText: String?,
    val alreadySeenFullContent: Boolean?,
    val isExpanded: Boolean,
    var attachments: List<AttachmentViewData>,
    var communityId: Int,
    var isPinned: Boolean,
    var isSaved: Boolean,
    var isLiked: Boolean,
    var userId: String,
    var likesCount: Int,
    var commentsCount: Int,
    var menuItems: List<OverflowMenuItemViewData>,
    var comments: List<CommentViewData>,
    var createdAt: Long,
    var updatedAt: Long,
    var user: UserViewData
) : Parcelable, BaseViewType {

    //TODO: add post id while adding menu item
    //TODO: limit for short text

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
            (attachments.isNotEmpty() && attachments.first().attachmentType == LINK) -> {
                ITEM_POST_LINK
            }
            else -> {
                ITEM_POST_TEXT_ONLY
            }
        }

    class Builder {
        private var id: String = ""
        private var text: String = ""
        private var shortText: String? = null
        private var alreadySeenFullContent: Boolean? = null
        private var isExpanded: Boolean = false
        private var attachments: List<AttachmentViewData> = listOf()
        private var communityId: Int = 0
        private var isPinned: Boolean = false
        private var isSaved: Boolean = false
        private var isEdited: Boolean = false
        private var isLiked: Boolean = false
        private var userId: String = ""
        private var likesCount: Int = 0
        private var commentsCount: Int = 0
        private var menuItems: List<OverflowMenuItemViewData> = listOf()
        private var comments: List<CommentViewData> = listOf()
        private var createdAt: Long = 0
        private var updatedAt: Long = 0
        private var user: UserViewData = UserViewData.Builder().build()

        fun id(id: String) = apply { this.id = id }
        fun text(text: String) = apply { this.text = text }
        fun shortText(shortText: String?) = apply { this.shortText = shortText }
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
        fun userId(userId: String) = apply { this.userId = userId }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }
        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }
        fun menuItems(menuItems: List<OverflowMenuItemViewData>) =
            apply { this.menuItems = menuItems }

        fun comments(comments: List<CommentViewData>) = apply { this.comments = comments }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun user(user: UserViewData) = apply { this.user = user }

        fun build() = PostViewData(
            id,
            text,
            shortText,
            alreadySeenFullContent,
            isExpanded,
            attachments,
            communityId,
            isPinned,
            isSaved,
            isEdited,
            userId,
            likesCount,
            commentsCount,
            menuItems,
            comments,
            createdAt,
            updatedAt,
            user
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .text(text)
            .shortText(SeeMoreUtil.getShortContent(text, 10))
            .alreadySeenFullContent(alreadySeenFullContent)
            .isExpanded(isExpanded)
            .attachments(attachments)
            .communityId(communityId)
            .isPinned(isPinned)
            .isSaved(isSaved)
            .isLiked(isLiked)
            .userId(userId)
            .likesCount(likesCount)
            .commentsCount(commentsCount)
            .menuItems(menuItems)
            .comments(comments)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .user(user)
    }
}