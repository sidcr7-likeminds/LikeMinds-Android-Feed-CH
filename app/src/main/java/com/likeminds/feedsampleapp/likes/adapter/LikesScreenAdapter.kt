package com.likeminds.feedsampleapp.likes.adapter

import com.likeminds.feedsampleapp.likes.adapter.databinder.ItemLikesScreenViewDataBinder
import com.likeminds.feedsampleapp.likes.model.LikeViewData
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

class LikesScreenAdapter : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, LikeViewData>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, LikeViewData>>(1)

        val likesScreenBinding = ItemLikesScreenViewDataBinder()
        viewDataBinders.add(likesScreenBinding)

        return viewDataBinders
    }
}