package com.likeminds.feedsample.post.detail.view.adapter

import com.likeminds.feedsample.post.detail.view.adapter.databinder.ItemNoCommentsFoundViewDataBinder
import com.likeminds.feedsample.post.detail.view.adapter.databinder.ItemPostDetailCommentViewDataBinder
import com.likeminds.feedsample.post.detail.view.adapter.databinder.ItemPostDetailCommentsCountViewDataBinder
import com.likeminds.feedsample.posttypes.model.UserViewData
import com.likeminds.feedsample.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsample.posttypes.view.adapter.databinder.*
import com.likeminds.feedsample.utils.ValueUtils.getItemInList
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

class PostDetailAdapter constructor(
    val postAdapterListener: PostAdapterListener,
    val postDetailAdapterListener: PostDetailAdapterListener,
    val postDetailReplyAdapterListener: PostDetailReplyAdapter.PostDetailReplyAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(8)

        val itemPostDetailCommentsCountViewDataBinder = ItemPostDetailCommentsCountViewDataBinder()
        viewDataBinders.add(itemPostDetailCommentsCountViewDataBinder)

        val itemPostDetailCommentViewDataBinder =
            ItemPostDetailCommentViewDataBinder(
                postDetailAdapterListener,
                postDetailReplyAdapterListener
            )
        viewDataBinders.add(itemPostDetailCommentViewDataBinder)

        val itemPostTextOnlyBinder = ItemPostTextOnlyViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostTextOnlyBinder)

        val itemPostSingleImageViewDataBinder =
            ItemPostSingleImageViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostSingleImageViewDataBinder)

        val itemPostSingleVideoViewDataBinder =
            ItemPostSingleVideoViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostSingleVideoViewDataBinder)

        val itemPostLinkViewDataBinder = ItemPostLinkViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostLinkViewDataBinder)

        val itemPostDocumentsViewDataBinder = ItemPostDocumentsViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostDocumentsViewDataBinder)

        val itemPostMultipleMediaViewDataBinder =
            ItemPostMultipleMediaViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostMultipleMediaViewDataBinder)

        val itemNoCommentsFoundBinder =
            ItemNoCommentsFoundViewDataBinder()
        viewDataBinders.add(itemNoCommentsFoundBinder)

        return viewDataBinders
    }

    operator fun get(position: Int): BaseViewType? {
        return items().getItemInList(position)
    }

    interface PostDetailAdapterListener {
        fun updateCommentSeenFullContent(
            position: Int,
            alreadySeenFullContent: Boolean,
            parentCommentId: String?
        )

        fun likeComment(commentId: String)
        fun fetchReplies(commentId: String)
        fun replyOnComment(commentId: String, commentPosition: Int, parentCommenter: UserViewData)
        fun onCommentMenuItemClicked(
            postId: String,
            commentId: String,
            creatorId: String,
            menuId: Int
        )

        fun showLikesScreen(postId: String, commentId: String)
    }
}