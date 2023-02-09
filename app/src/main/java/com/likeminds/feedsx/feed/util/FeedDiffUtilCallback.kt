package com.likeminds.feedsx.feed.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.util.PostDiffUtilHelper.postViewData
import com.likeminds.feedsx.utils.model.BaseViewType

class FeedDiffUtilCallback(
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

        if (oldItem is PostViewData && newItem is PostViewData) {
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
        if (oldItem is PostViewData && newItem is PostViewData) {
            return postViewData(
                oldItem,
                newItem
            )
        }
        return false
    }
}