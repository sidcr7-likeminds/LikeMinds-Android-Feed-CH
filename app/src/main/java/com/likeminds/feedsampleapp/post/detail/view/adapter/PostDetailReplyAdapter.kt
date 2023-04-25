package com.likeminds.feedsampleapp.post.detail.view.adapter

import com.likeminds.feedsampleapp.post.detail.view.adapter.databinder.ItemPostDetailReplyViewDataBinder
import com.likeminds.feedsampleapp.post.detail.view.adapter.databinder.ItemReplyViewMoreReplyViewDataBinder
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

class PostDetailReplyAdapter constructor(
    val postDetailAdapterListener: PostDetailAdapter.PostDetailAdapterListener,
    val postDetailReplyAdapterListener: PostDetailReplyAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val itemPostDetailReplyViewDataBinder = ItemPostDetailReplyViewDataBinder(
            postDetailAdapterListener,
            postDetailReplyAdapterListener
        )
        viewDataBinders.add(itemPostDetailReplyViewDataBinder)

        val itemReplyViewMoreReplyViewDataBinder =
            ItemReplyViewMoreReplyViewDataBinder(postDetailReplyAdapterListener)
        viewDataBinders.add(itemReplyViewMoreReplyViewDataBinder)

        return viewDataBinders
    }

    interface PostDetailReplyAdapterListener {
        fun likeReply(parentCommentId: String, replyId: String)
        fun viewMoreReplies(
            parentCommentId: String,
            page: Int
        )

        fun onReplyMenuItemClicked(
            postId: String,
            parentCommentId: String,
            replyId: String,
            creatorId: String,
            menuId: Int
        )
    }
}