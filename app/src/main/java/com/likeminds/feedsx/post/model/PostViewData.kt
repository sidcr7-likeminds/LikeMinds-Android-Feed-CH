package com.likeminds.feedsx.post.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.*
import kotlinx.parcelize.Parcelize

@Parcelize
class PostViewData private constructor(
    var id: String,
    var text: String,
    var attachments: List<AttachmentViewData>,
    var communityId: Int,
    var isPinned: Boolean,
    var isSaved: Boolean,
    var isEdited: Boolean,
    var userId: String,
    var likesCount: Int,
    var commentsCount: Int,
    var menuItems: List<MenuItemViewData>,
    var comments: List<CommentViewData>,
    var createdAt: Long,
    var updatedAt: Long,
    var user: UserViewData
) : Parcelable, BaseViewType {

    //TODO: isEdited not added in ED yet.

    override val viewType: Int
        get() = when {
            //TODO: For Link?
            (attachments.size == 1 && attachments.first().fileType == 1) -> {
                ITEM_POST_SINGLE_IMAGE
            }
            (attachments.size == 1 && attachments.first().fileType == 2) -> {
                ITEM_POST_SINGLE_VIDEO
            }
            (attachments.isNotEmpty() && attachments.first().fileType == 3) -> {
                ITEM_POST_DOCUMENTS
            }
            (attachments.size > 1 && (attachments.first().fileType == 1 || attachments.first().fileType == 2)) -> {
                ITEM_POST_MULTIPLE_MEDIA
            }
            else -> {
                ITEM_POST_TEXT_ONLY
            }
        }

    class Builder {
        private var id: String = ""
        private var text: String = ""
        private var attachments: List<AttachmentViewData> = listOf()
        private var communityId: Int = 0
        private var isPinned: Boolean = false
        private var isSaved: Boolean = false
        private var isEdited: Boolean = false
        private var userId: String = ""
        private var likesCount: Int = 0
        private var commentsCount: Int = 0
        private var menuItems: List<MenuItemViewData> = listOf()
        private var comments: List<CommentViewData> = listOf()
        private var createdAt: Long = 0
        private var updatedAt: Long = 0
        private var user: UserViewData = UserViewData.Builder().build()

        fun id(id: String) = apply { this.id = id }
        fun text(text: String) = apply { this.text = text }
        fun attachments(attachments: List<AttachmentViewData>) =
            apply { this.attachments = attachments }

        fun communityId(communityId: Int) = apply { this.communityId = communityId }
        fun isPinned(isPinned: Boolean) = apply { this.isPinned = isPinned }
        fun isSaved(isSaved: Boolean) = apply { this.isSaved = isSaved }
        fun isEdited(isEdited: Boolean) = apply { this.isEdited = isEdited }
        fun userId(userId: String) = apply { this.userId = userId }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }
        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }
        fun menuItems(menuItems: List<MenuItemViewData>) = apply { this.menuItems = menuItems }
        fun comments(comments: List<CommentViewData>) = apply { this.comments = comments }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun user(user: UserViewData) = apply { this.user = user }

        fun build() = PostViewData(
            id,
            text,
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
            .attachments(attachments)
            .communityId(communityId)
            .isPinned(isPinned)
            .isSaved(isSaved)
            .isEdited(isEdited)
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