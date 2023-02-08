package com.likeminds.feedsx.post.detail.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.model.BaseViewType

class PostDetailDiffUtilCallback(
    private val oldList: List<BaseViewType>,
    private val newList: List<BaseViewType>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem is CommentsCountViewData && newItem is CommentsCountViewData -> {
                oldItem.commentsCount == newItem.commentsCount
            }
            oldItem is PostViewData && newItem is PostViewData -> {
                oldItem.id == newItem.id
            }
            oldItem is CommentViewData && newItem is CommentViewData -> {
                oldItem.id == newItem.id
            }
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem is CommentsCountViewData && newItem is CommentsCountViewData -> {
                commentsCountViewData(
                    oldItem,
                    newItem
                )
            }
            oldItem is PostViewData && newItem is PostViewData -> {
                // TODO: Confirm
                return true
            }
            oldItem is CommentViewData && newItem is CommentViewData -> {
                commentViewData(
                    oldItem,
                    newItem
                ) && commentViewDataList(
                    oldItem.replies,
                    newItem.replies
                )
            }
            else -> false
        }
    }

    private fun commentViewDataList(
        oldItem: MutableList<CommentViewData>,
        newItem: MutableList<CommentViewData>
    ): Boolean {
        if (oldItem.isEmpty() && newItem.isEmpty()) return true
        if (oldItem.size != newItem.size) return false
        for (i in oldItem.indices) {
            if (!commentViewData(oldItem[i], newItem[i])) return false
        }
        return true
    }

    private fun commentViewData(
        oldItem: CommentViewData,
        newItem: CommentViewData
    ): Boolean {
        return oldItem.postId == newItem.postId
                && oldItem.isLiked == newItem.isLiked
                && oldItem.userId == newItem.userId
                && oldItem.text == newItem.text
                && oldItem.level == newItem.level
                && oldItem.likesCount == newItem.likesCount
                && oldItem.repliesCount == newItem.repliesCount
                && oldItem.createdAt == newItem.createdAt
                && oldItem.updatedAt == newItem.updatedAt
                && oldItem.parentId == newItem.parentId
                && userViewData(oldItem.user, newItem.user)
                && overflowMenuItemViewDataList(oldItem.menuItems, newItem.menuItems)
    }

    private fun commentsCountViewData(
        oldItem: CommentsCountViewData,
        newItem: CommentsCountViewData
    ): Boolean {
        return oldItem.commentsCount == newItem.commentsCount
    }

    //TODO: move to util?
    private fun userViewData(
        oldItem: UserViewData,
        newItem: UserViewData
    ): Boolean {
        return oldItem.id == newItem.id
                && oldItem.name == newItem.name
                && oldItem.imageUrl == newItem.imageUrl
                && oldItem.userUniqueId == newItem.userUniqueId
                && oldItem.customTitle == newItem.customTitle
                && oldItem.isGuest == newItem.isGuest
                && oldItem.isDeleted == newItem.isDeleted
    }

    private fun overflowMenuItemViewDataList(
        oldItem: List<OverflowMenuItemViewData>,
        newItem: List<OverflowMenuItemViewData>
    ): Boolean {
        if (oldItem.isEmpty() && newItem.isEmpty()) return true
        if (oldItem.size != newItem.size) return false
        for (i in oldItem.indices) {
            if (!overflowMenuItemViewData(oldItem[i], newItem[i])) return false
        }
        return true
    }

    private fun overflowMenuItemViewData(
        oldItem: OverflowMenuItemViewData,
        newItem: OverflowMenuItemViewData
    ): Boolean {
        return oldItem.title == newItem.title
                && oldItem.entityId == newItem.entityId
    }
}