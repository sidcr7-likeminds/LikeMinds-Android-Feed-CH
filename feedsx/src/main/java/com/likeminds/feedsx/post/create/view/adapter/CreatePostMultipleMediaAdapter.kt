package com.likeminds.feedsx.post.create.view.adapter

import com.likeminds.feedsx.post.create.util.CreatePostListener
import com.likeminds.feedsx.post.create.view.adapter.databinder.createpostmultiplemedia.LMFeedItemCreatePostMultipleMediaImageViewDataBinder
import com.likeminds.feedsx.post.create.view.adapter.databinder.createpostmultiplemedia.LMFeedItemCreatePostMultipleMediaVideoViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class CreatePostMultipleMediaAdapter constructor(
    private val listener: CreatePostListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val createPostMultipleMediaImageViewDataBinder =
            LMFeedItemCreatePostMultipleMediaImageViewDataBinder(listener)
        viewDataBinders.add(createPostMultipleMediaImageViewDataBinder)

        val createPostMultipleMediaVideoViewDataBinder =
            LMFeedItemCreatePostMultipleMediaVideoViewDataBinder(listener)
        viewDataBinders.add(createPostMultipleMediaVideoViewDataBinder)

        return viewDataBinders
    }
}