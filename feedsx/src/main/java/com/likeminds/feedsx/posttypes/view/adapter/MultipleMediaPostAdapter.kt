package com.likeminds.feedsx.posttypes.view.adapter

import com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia.LMFeedItemMultipleMediaImageViewDataBinder
import com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia.LMFeedItemMultipleMediaVideoViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class MultipleMediaPostAdapter(val listener: PostAdapterListener) :
    BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val multipleMediaImageBinder = LMFeedItemMultipleMediaImageViewDataBinder(listener)
        viewDataBinders.add(multipleMediaImageBinder)

        val multipleMediaVideoBinder = LMFeedItemMultipleMediaVideoViewDataBinder(listener)
        viewDataBinders.add(multipleMediaVideoBinder)

        return viewDataBinders
    }
}