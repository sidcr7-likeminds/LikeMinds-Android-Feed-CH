package com.likeminds.feedsx.post.create.view.adapter

import com.likeminds.feedsx.post.create.util.CreatePostListener
import com.likeminds.feedsx.post.create.view.adapter.databinder.createpostmultiplemedia.ItemCreatePostMultipleMediaImageViewDataBinder
import com.likeminds.feedsx.post.create.view.adapter.databinder.createpostmultiplemedia.ItemCreatePostMultipleMediaVideoViewDataBinder
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
            ItemCreatePostMultipleMediaImageViewDataBinder(listener)
        viewDataBinders.add(createPostMultipleMediaImageViewDataBinder)

        val createPostMultipleMediaVideoViewDataBinder =
            ItemCreatePostMultipleMediaVideoViewDataBinder(listener)
        viewDataBinders.add(createPostMultipleMediaVideoViewDataBinder)

        return viewDataBinders
    }
}