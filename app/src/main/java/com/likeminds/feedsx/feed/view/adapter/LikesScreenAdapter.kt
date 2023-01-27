package com.likeminds.feedsx.feed.view.adapter

import com.likeminds.feedsx.feed.view.model.LikesViewData
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class LikesScreenAdapter : BaseRecyclerAdapter<BaseViewType>() {
    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, LikesViewData>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, LikesViewData>>(1)

        val likesScreenBinding = ItemLikesScreenViewDataBinder()
        viewDataBinders.add(likesScreenBinding)

        return viewDataBinders
    }
}