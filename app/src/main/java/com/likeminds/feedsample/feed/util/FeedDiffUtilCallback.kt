package com.likeminds.feedsample.feed.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.feedsample.posttypes.model.PostViewData
import com.likeminds.feedsample.posttypes.util.PostDiffUtilHelper.postViewData
import com.likeminds.feedsample.utils.model.BaseViewType

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