package com.likeminds.feedsample.notificationfeed.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.feedsample.notificationfeed.model.NotificationFeedViewData
import com.likeminds.feedsample.posttypes.util.PostDiffUtilHelper.overflowMenuItemViewDataList
import com.likeminds.feedsample.posttypes.util.PostDiffUtilHelper.userViewData
import com.likeminds.feedsample.utils.model.BaseViewType

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

        if (oldItem is NotificationFeedViewData && newItem is NotificationFeedViewData) {
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
        if (oldItem is NotificationFeedViewData && newItem is NotificationFeedViewData) {
            return notificationViewData(
                oldItem,
                newItem
            )
        }
        return false
    }

    private fun notificationViewData(
        oldItem: NotificationFeedViewData,
        newItem: NotificationFeedViewData
    ): Boolean {
        return oldItem.id == newItem.id
                && oldItem.isRead && newItem.isRead
                && oldItem.actionBy == newItem.actionBy
                && actionOnList(oldItem.actionOn, newItem.actionOn)
                && oldItem.entityType == newItem.entityType
                && oldItem.entityId == newItem.entityId
                && oldItem.action == newItem.action
                && oldItem.cta == newItem.cta
                && oldItem.activityMessage == newItem.activityMessage
                && userViewData(oldItem.user, newItem.user)
                && overflowMenuItemViewDataList(oldItem.menuItems, newItem.menuItems)
                && oldItem.createdAt == newItem.createdAt
                && oldItem.updatedAt == newItem.updatedAt
    }

    private fun actionOnList(
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
}