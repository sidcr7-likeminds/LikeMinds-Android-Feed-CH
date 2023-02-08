package com.likeminds.feedsx.feed.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.utils.model.BaseViewType

class FeedDiffUtilCallback(
    private val oldList: List<BaseViewType>,
    private val newList: List<BaseViewType>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem is PostViewData && newItem is PostViewData) {
            return (oldItem.id == newItem.id)
        }
        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is PostViewData && newItem is PostViewData) {
            return postViewData(
                oldItem,
                newItem
            )
        }
        return false
    }

    private fun postViewData(
        oldItem: PostViewData,
        newItem: PostViewData
    ): Boolean {
        return oldItem.text == newItem.text
                && oldItem.shortText == newItem.shortText
                && oldItem.alreadySeenFullContent == newItem.alreadySeenFullContent
                && oldItem.isExpanded == newItem.isExpanded
                && oldItem.isPinned == newItem.isPinned
                && oldItem.communityId == newItem.communityId
                && oldItem.isSaved == newItem.isSaved
                && oldItem.isLiked == newItem.isLiked
                && oldItem.userId == newItem.userId
                && oldItem.likesCount == newItem.likesCount
                && oldItem.commentsCount == newItem.commentsCount
                && oldItem.createdAt == newItem.createdAt
                && oldItem.updatedAt == newItem.updatedAt
                && userViewData(oldItem.user, newItem.user)
                && overflowMenuItemViewDataList(oldItem.menuItems, newItem.menuItems)
                && attachmentViewDataList(oldItem.attachments, newItem.attachments)
    }

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

    private fun attachmentViewDataList(
        oldItem: List<AttachmentViewData>,
        newItem: List<AttachmentViewData>
    ): Boolean {
        if (oldItem.isEmpty() && newItem.isEmpty()) return true
        if (oldItem.size != newItem.size) return false
        for (i in oldItem.indices) {
            if (!attachmentViewData(oldItem[i], newItem[i])) return false
        }
        return true
    }

    private fun attachmentViewData(
        oldItem: AttachmentViewData,
        newItem: AttachmentViewData
    ): Boolean {
        return oldItem.attachmentType == newItem.attachmentType
                && oldItem.dynamicViewType == newItem.dynamicViewType
                && attachmentMetaViewData(oldItem.attachmentMeta, newItem.attachmentMeta)
    }

    private fun attachmentMetaViewData(
        oldItem: AttachmentMetaViewData,
        newItem: AttachmentMetaViewData
    ): Boolean {
        return oldItem.url == newItem.url
                && oldItem.size == newItem.size
                && oldItem.duration == newItem.duration
                && oldItem.pageCount == newItem.pageCount
                && linkOGTagsViewData(oldItem.ogTags, newItem.ogTags)
    }

    private fun linkOGTagsViewData(
        oldItem: LinkOGTags,
        newItem: LinkOGTags
    ): Boolean {
        return oldItem.url == newItem.url
                && oldItem.description == newItem.description
                && oldItem.image == newItem.image
                && oldItem.title == newItem.title
    }
}