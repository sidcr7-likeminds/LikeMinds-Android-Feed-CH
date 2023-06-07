package com.likeminds.feedsx.notificationfeed.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.feedsx.notificationfeed.model.ActivityEntityViewData
import com.likeminds.feedsx.notificationfeed.model.ActivityViewData
import com.likeminds.feedsx.posttypes.util.PostDiffUtilHelper.userViewData
import com.likeminds.feedsx.utils.model.BaseViewType

class NotificationFeedDiffUtilCallback(
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

        if (oldItem is ActivityViewData && newItem is ActivityViewData) {
            return (oldItem.id == newItem.id)
        }
        return false
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is ActivityViewData && newItem is ActivityViewData) {
            return notificationViewData(
                oldItem,
                newItem
            )
        }
        return false
    }

    private fun notificationViewData(
        oldItem: ActivityViewData,
        newItem: ActivityViewData
    ): Boolean {
        return oldItem.id == newItem.id
                && oldItem.isRead && newItem.isRead
                && oldItem.actionOn == newItem.actionOn
                && actionByList(oldItem.actionBy, newItem.actionBy)
                && oldItem.entityType == newItem.entityType
                && oldItem.entityId == newItem.entityId
                && oldItem.entityOwnerId == newItem.entityOwnerId
                && oldItem.action == newItem.action
                && oldItem.cta == newItem.cta
                && oldItem.activityText == newItem.activityText
                && activityEntityViewData(oldItem.activityEntityData, newItem.activityEntityData)
                && userViewData(oldItem.user, newItem.user)
                && oldItem.createdAt == newItem.createdAt
                && oldItem.updatedAt == newItem.updatedAt
    }

    private fun actionByList(
        oldItem: List<String>,
        newItem: List<String>
    ): Boolean {
        if (oldItem.isEmpty() && newItem.isEmpty()) return true
        if (oldItem.size != newItem.size) return false
        for (i in oldItem.indices) {
            if (oldItem[i] != newItem[i]) return false
        }
        return true
    }

    private fun activityEntityViewData(
        oldItem: ActivityEntityViewData,
        newItem: ActivityEntityViewData
    ): Boolean {
        return oldItem.id == newItem.id
                && oldItem.text == newItem.text
                && oldItem.deleteReason == newItem.deleteReason
                && oldItem.deletedBy == newItem.deletedBy
                && oldItem.heading == newItem.heading
                && oldItem.isEdited == newItem.isEdited
                && oldItem.isPinned == newItem.isPinned
                && oldItem.userId == newItem.userId
                && oldItem.level == newItem.level
                && oldItem.createdAt == newItem.createdAt
                && oldItem.updatedAt == newItem.updatedAt
    }
}