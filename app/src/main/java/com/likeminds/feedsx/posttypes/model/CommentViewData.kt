package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENT
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_REPLY
import kotlinx.parcelize.Parcelize

@Parcelize
class CommentViewData private constructor(
    var id: String,
    var postId: String,
    var isLiked: Boolean,
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
    val alreadySeenFullContent: Boolean?,
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
        private var alreadySeenFullContent: Boolean? = null

        fun id(id: String) = apply { this.id = id }
        fun postId(postId: String) = apply { this.postId = postId }
        fun isLiked(isLiked: Boolean) = apply { this.isLiked = isLiked }
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

        fun replies(replies: MutableList<CommentViewData>) =
            apply { this.replies = replies }

        fun alreadySeenFullContent(alreadySeenFullContent: Boolean?) =
            apply { this.alreadySeenFullContent = alreadySeenFullContent }

        fun build() = CommentViewData(
            id,
            postId,
            isLiked,
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
            alreadySeenFullContent
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .postId(postId)
            .isLiked(isLiked)
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
            .alreadySeenFullContent(alreadySeenFullContent)
    }
}