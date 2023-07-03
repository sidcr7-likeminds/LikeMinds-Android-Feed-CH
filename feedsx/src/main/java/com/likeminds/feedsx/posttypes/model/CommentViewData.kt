package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENT
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_REPLY
import kotlinx.parcelize.Parcelize

@Parcelize
class CommentViewData private constructor(
    val id: String,
    val postId: String,
    val isLiked: Boolean,
    val isEdited: Boolean,
    val userId: String,
    val text: String,
    val level: Int,
    val likesCount: Int,
    val repliesCount: Int,
    val user: UserViewData,
    val createdAt: Long,
    val updatedAt: Long,
    val menuItems: List<OverflowMenuItemViewData>,
    val replies: MutableList<CommentViewData>,
    val parentId: String?,
    val parentComment: CommentViewData?,
    val alreadySeenFullContent: Boolean?,
    val fromCommentLiked: Boolean,
    val fromCommentEdited: Boolean,
    val uuid: String
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
        private var uuid: String = ""

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

        fun uuid(uuid: String) = apply { this.uuid = uuid }

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
            fromCommentEdited,
            uuid
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
            .uuid(uuid)
    }
}