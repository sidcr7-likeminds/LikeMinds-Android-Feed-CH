package com.likeminds.feedsx.post.adapter

import com.likeminds.feedsx.post.adapter.databinder.*
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class PostAdapter : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(6)

        val postTextOnlyBinding = PostTextOnlyItemViewDataBinder()
        viewDataBinders.add(postTextOnlyBinding)

        val postSingleImageItemViewDataBinder = PostSingleImageItemViewDataBinder()
        viewDataBinders.add(postSingleImageItemViewDataBinder)

        val postSingleVideoItemViewDataBinder = PostSingleVideoItemViewDataBinder()
        viewDataBinders.add(postSingleVideoItemViewDataBinder)

        val postLinkItemViewDataBinder = PostLinkItemViewDataBinder()
        viewDataBinders.add(postLinkItemViewDataBinder)

        val postDocumentsItemViewDataBinder = PostDocumentsItemViewDataBinder()
        viewDataBinders.add(postDocumentsItemViewDataBinder)

        val postMultipleMediaItemViewDataBinder = PostMultipleMediaItemViewDataBinder()
        viewDataBinders.add(postMultipleMediaItemViewDataBinder)

        return viewDataBinders
    }
}