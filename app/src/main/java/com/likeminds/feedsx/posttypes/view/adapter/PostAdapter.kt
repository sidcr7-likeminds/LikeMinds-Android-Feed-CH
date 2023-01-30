package com.likeminds.feedsx.posttypes.view.adapter

import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.view.adapter.databinder.*
import com.likeminds.feedsx.utils.ValueUtils.getItemInList
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class PostAdapter constructor(
    val listener: PostAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(6)

        val postTextOnlyBinding = ItemPostTextOnlyViewDataBinder(listener)
        viewDataBinders.add(postTextOnlyBinding)

        val itemPostSingleImageViewDataBinder = ItemPostSingleImageViewDataBinder(listener)
        viewDataBinders.add(itemPostSingleImageViewDataBinder)

        val itemPostSingleVideoViewDataBinder = ItemPostSingleVideoViewDataBinder(listener)
        viewDataBinders.add(itemPostSingleVideoViewDataBinder)

        val itemPostLinkViewDataBinder = ItemPostLinkViewDataBinder(listener)
        viewDataBinders.add(itemPostLinkViewDataBinder)

        val itemPostDocumentsViewDataBinder = ItemPostDocumentsViewDataBinder(listener)
        viewDataBinders.add(itemPostDocumentsViewDataBinder)

        val itemPostMultipleMediaViewDataBinder = ItemPostMultipleMediaViewDataBinder(listener)
        viewDataBinders.add(itemPostMultipleMediaViewDataBinder)

        return viewDataBinders
    }

    operator fun get(position: Int): BaseViewType? {
        return items().getItemInList(position)
    }

    interface PostAdapterListener {
        //TODO: add compulsory methods

        fun updateSeenFullContent(position: Int, alreadySeenFullContent: Boolean)
        fun pinPost() {}
        fun savePost() {}
        fun likePost() {}
        fun sharePost() {}
        fun comment() {}
        fun onPostMenuItemClicked(postId: String, title: String)
        fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int)
    }
}