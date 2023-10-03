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
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(7)

        val itemPostTextOnlyBinder = LMFeedItemPostTextOnlyViewDataBinder(listener)
        viewDataBinders.add(itemPostTextOnlyBinder)

        val itemPostSingleImageViewDataBinder = LMFeedItemPostSingleImageViewDataBinder(listener)
        viewDataBinders.add(itemPostSingleImageViewDataBinder)

        val itemPostSingleVideoViewDataBinder = LMFeedItemPostSingleVideoViewDataBinder(listener)
        viewDataBinders.add(itemPostSingleVideoViewDataBinder)

        val itemPostLinkViewDataBinder = LMFeedItemPostLinkViewDataBinder(listener)
        viewDataBinders.add(itemPostLinkViewDataBinder)

        val lmFeedItemPostDocumentsViewDataBinder = LMFeedItemPostDocumentsViewDataBinder(listener)
        viewDataBinders.add(lmFeedItemPostDocumentsViewDataBinder)

        val itemPostMultipleMediaViewDataBinder =
            LMFeedItemPostMultipleMediaViewDataBinder(listener)
        viewDataBinders.add(itemPostMultipleMediaViewDataBinder)

        return viewDataBinders
    }

    operator fun get(position: Int): BaseViewType? {
        return items().getItemInList(position)
    }
}

interface PostAdapterListener {
    fun updatePostSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        //triggered when a user clicks on "See More"
    }

    fun savePost(position: Int) {
        //triggered when a user clicks on save post icon
    }

    fun likePost(position: Int) {
        //triggered when a user clicks on like icon
    }

    fun sharePost(postId: String) {
        //triggered when a user clicks on share icon
    }

    fun comment(postId: String) {
        //triggered when a user clicks on add comments
    }

    fun onPostMenuItemClicked(
        postId: String,
        postCreatorUUID: String,
        menuId: Int
    ) {
        //triggered when a user clicks overflow menu of a post
    }

    fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        //triggered when a user clicks on "See More" of document type post
    }

    fun showLikesScreen(postId: String) {
        //triggered when a user clicks on no of likes
    }

    fun postDetail(postId: String) {
        //triggered when a user clicks on post to open post detail
    }

    fun updateFromLikedSaved(position: Int) {
        //triggered to update the data with re-inflation of the item
    }
}