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

    fun getPostById(postId: String): PostViewData {
        val items = items() as List<PostViewData>
        return items.filter {
            it.id == postId
        }[0]
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(6)

        val itemPostTextOnlyBinder = ItemPostTextOnlyViewDataBinder(listener)
        viewDataBinders.add(itemPostTextOnlyBinder)

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
        fun savePost(position: Int) {}
        fun likePost(position: Int) {}
        fun sharePost() {}
        fun comment(postId: String)
        // TODO: remove creator id
        fun onPostMenuItemClicked(postId: String, title: String, creatorId: String)
        fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int)
        fun showLikesScreen(postId: String)
        fun postDetail(postData: PostViewData) {}
        fun updateFromLikedSaved(position: Int) {}
    }
}