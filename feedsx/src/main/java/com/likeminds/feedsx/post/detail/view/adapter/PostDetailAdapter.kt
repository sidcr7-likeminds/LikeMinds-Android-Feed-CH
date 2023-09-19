package com.likeminds.feedsx.post.detail.view.adapter

import com.likeminds.feedsx.post.detail.view.adapter.databinder.*
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.posttypes.view.adapter.databinder.*
import com.likeminds.feedsx.utils.ValueUtils.getItemInList
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class PostDetailAdapter constructor(
    private val postAdapterListener: PostAdapterListener,
    private val postDetailAdapterListener: PostDetailAdapterListener,
    private val postDetailReplyAdapterListener: PostDetailReplyAdapter.PostDetailReplyAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(10)

        val itemPostDetailCommentsCountViewDataBinder = ItemPostDetailCommentsCountViewDataBinder()
        viewDataBinders.add(itemPostDetailCommentsCountViewDataBinder)

        val itemPostDetailCommentViewDataBinder =
            ItemPostDetailCommentViewDataBinder(
                postDetailAdapterListener,
                postDetailReplyAdapterListener
            )
        viewDataBinders.add(itemPostDetailCommentViewDataBinder)

        val lmFeedItemPostTextOnlyBinder = LMFeedItemPostTextOnlyViewDataBinder(postAdapterListener)
        viewDataBinders.add(lmFeedItemPostTextOnlyBinder)

        val lmFeedItemPostSingleImageViewDataBinder =
            LMFeedItemPostSingleImageViewDataBinder(postAdapterListener)
        viewDataBinders.add(lmFeedItemPostSingleImageViewDataBinder)

        val lmFeedItemPostSingleVideoViewDataBinder =
            LMFeedItemPostDetailSingleVideoViewDataBinder(postAdapterListener)
        viewDataBinders.add(lmFeedItemPostSingleVideoViewDataBinder)

        val lmFeedItemPostLinkViewDataBinder =
            LMFeedItemPostDetailLinkViewDataBinder(postAdapterListener)
        viewDataBinders.add(lmFeedItemPostLinkViewDataBinder)

        val lmFeedItemPostArticleViewDataBinder =
            LMFeedItemPostDetailArticleViewDataBinder(postAdapterListener)
        viewDataBinders.add(lmFeedItemPostArticleViewDataBinder)

        val lmFeedItemPostDocumentsViewDataBinder =
            LMFeedItemPostDetailDocumentsViewDataBinder(postAdapterListener)
        viewDataBinders.add(lmFeedItemPostDocumentsViewDataBinder)

        val itemPostMultipleMediaViewDataBinder =
            LMFeedItemPostMultipleMediaViewDataBinder(postAdapterListener)
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
            commentCreatorUUID: String,
            menuId: Int
        )

        fun showLikesScreen(postId: String, commentId: String)
    }
}