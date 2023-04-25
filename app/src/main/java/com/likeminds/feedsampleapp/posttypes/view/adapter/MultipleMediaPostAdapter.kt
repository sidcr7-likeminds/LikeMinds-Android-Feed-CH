package com.likeminds.feedsampleapp.posttypes.view.adapter

import com.likeminds.feedsampleapp.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaImageViewDataBinder
import com.likeminds.feedsampleapp.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaVideoViewDataBinder
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

class MultipleMediaPostAdapter : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val multipleMediaImageBinder = ItemMultipleMediaImageViewDataBinder()
        viewDataBinders.add(multipleMediaImageBinder)

        val multipleMediaVideoBinder = ItemMultipleMediaVideoViewDataBinder()
        viewDataBinders.add(multipleMediaVideoBinder)

        return viewDataBinders
    }
}