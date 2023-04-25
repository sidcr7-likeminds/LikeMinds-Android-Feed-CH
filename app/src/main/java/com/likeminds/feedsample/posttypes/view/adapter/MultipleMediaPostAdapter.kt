package com.likeminds.feedsample.posttypes.view.adapter

import com.likeminds.feedsample.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaImageViewDataBinder
import com.likeminds.feedsample.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaVideoViewDataBinder
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

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