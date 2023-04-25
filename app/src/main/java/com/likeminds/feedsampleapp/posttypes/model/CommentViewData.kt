package com.likeminds.feedsampleapp.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsampleapp.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsampleapp.utils.model.BaseViewType
import com.likeminds.feedsampleapp.utils.model.ITEM_POST_DETAIL_COMMENT
import com.likeminds.feedsampleapp.utils.model.ITEM_POST_DETAIL_REPLY
import kotlinx.parcelize.Parcelize

@Parcelize
class CommentViewData private constructor(
    var id: String,
    var postId: String,
    var isLiked: Boolean,
    var isEdited: Boolean,
    var userId: String,
    var text: String,
    var level: Int,
    var likesCount: Int,
    var repliesCount: Int,
    var user: UserViewData,
    var createdAt: Long,
    var updatedAt: Long,
    var menuItems: List<OverflowMenuItemViewData>,
    var replies: MutableList<CommentViewData>,
    var parentId: String?,
    var parentComment: CommentViewData?,
    var alreadySeenFullContent: Boolean?,
    var fromCommentLiked: Boolean,
    var fromCommentEdited: Boolean
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = when (level) {
            0 -> ITEM_POST_DETAIL_COMMENT
            else -> ITEM_POST_DETAIL_REPLY
        }

    class Builder {
        private var id: String = ""
        private var postId: String = ""
        private var isLiked: Boolean = false
        private var isEdited: Boolean = false
        private var userId: String = ""
        private var text: String = ""
        private var level: Int = 0
        private var likesCount: Int = 0
        private var repliesCount: Int = 0
        private var user: UserViewData = UserViewData.Builder().build()
        private var createdAt: Long = 0
        private var updatedAt: Long = 0
        private var menuItems: List<OverflowMenuItemViewData> = listOf()
        private var replies: MutableList<CommentViewData> = mutableListOf()
        private var parentId: String? = null
        private var parentComment: CommentViewData? = null
        private var alreadySeenFullContent: Boolean? = null
        private var fromCommentLiked: Boolean = false
        private var fromCommentEdited: Boolean = false

        fun id(id: String) = apply { this.id = id }
        fun postId(postId: String) = apply { this.postId = postId }
        fun isLiked(isLiked: Boolean) = apply { this.isLiked = isLiked }
        fun isEdited(isEdited: Boolean) = apply { this.isEdited = isEdited }
        fun userId(userId: String) = apply { this.userId = userId }
        fun text(text: String) = apply { this.text = text }
        fun level(level: Int) = apply { this.level = level }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }
        fun repliesCount(repliesCount: Int) = apply { this.repliesCount = repliesCount }
        fun user(user: UserViewData) = apply { this.user = user }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun menuItems(menuItems: List<OverflowMenuItemViewData>) =
            apply { this.menuItems = menuItems }

        fun parentId(parentId: String?) = apply { this.parentId = parentId }
        fun parentComment(parentComment: CommentViewData?) =
            apply { this.parentComment = parentComment }

        fun replies(replies: MutableList<CommentViewData>) =
            apply { this.replies = replies }

        fun alreadySeenFullContent(alreadySeenFullContent: Boolean?) =
            apply { this.alreadySeenFullContent = alreadySeenFullContent }

        fun fromCommentLiked(fromCommentLiked: Boolean) =
            apply { this.fromCommentLiked = fromCommentLiked }

        fun fromCommentEdited(fromCommentEdited: Boolean) =
            apply { this.fromCommentEdited = fromCommentEdited }

        fun build() = CommentViewData(
            id,
            postId,
            isLiked,
            isEdited,
            userId,
            text,
            level,
            likesCount,
            repliesCount,
            user,
            createdAt,
            updatedAt,
            menuItems,
            replies,
            parentId,
            parentComment,
            alreadySeenFullContent,
            fromCommentLiked,
            fromCommentEdited
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .postId(postId)
            .isLiked(isLiked)
            .isEdited(isEdited)
            .userId(userId)
            .text(text)
            .level(level)
            .likesCount(likesCount)
            .repliesCount(repliesCount)
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .menuItems(menuItems)
            .replies(replies)
            .parentId(parentId)
            .parentComment(parentComment)
            .alreadySeenFullContent(alreadySeenFullContent)
            .fromCommentLiked(fromCommentLiked)
            .fromCommentEdited(fromCommentEdited)
    }
}