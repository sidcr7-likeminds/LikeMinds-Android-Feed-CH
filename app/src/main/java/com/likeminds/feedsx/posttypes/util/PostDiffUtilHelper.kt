package com.likeminds.feedsx.posttypes.util

import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.posttypes.model.*

object PostDiffUtilHelper {

    fun postViewData(
        oldItem: PostViewData,
        newItem: PostViewData
    ): Boolean {
        return oldItem.text == newItem.text
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

    fun userViewData(
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

    fun overflowMenuItemViewDataList(
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

    fun overflowMenuItemViewData(
        oldItem: OverflowMenuItemViewData,
        newItem: OverflowMenuItemViewData
    ): Boolean {
        return oldItem.title == newItem.title
                && oldItem.entityId == newItem.entityId
    }

    fun attachmentViewDataList(
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

    fun attachmentViewData(
        oldItem: AttachmentViewData,
        newItem: AttachmentViewData
    ): Boolean {
        return oldItem.attachmentType == newItem.attachmentType
                && oldItem.dynamicViewType == newItem.dynamicViewType
                && attachmentMetaViewData(oldItem.attachmentMeta, newItem.attachmentMeta)
    }

    fun attachmentMetaViewData(
        oldItem: AttachmentMetaViewData,
        newItem: AttachmentMetaViewData
    ): Boolean {
        return oldItem.url == newItem.url
                && oldItem.size == newItem.size
                && oldItem.duration == newItem.duration
                && oldItem.pageCount == newItem.pageCount
                && linkOGTagsViewData(oldItem.ogTags, newItem.ogTags)
    }

    fun linkOGTagsViewData(
        oldItem: LinkOGTags,
        newItem: LinkOGTags
    ): Boolean {
        return oldItem.url == newItem.url
                && oldItem.description == newItem.description
                && oldItem.image == newItem.image
                && oldItem.title == newItem.title
    }
}