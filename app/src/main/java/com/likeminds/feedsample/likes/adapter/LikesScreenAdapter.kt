package com.likeminds.feedsample.likes.adapter

import com.likeminds.feedsample.likes.adapter.databinder.ItemLikesScreenViewDataBinder
import com.likeminds.feedsample.likes.model.LikeViewData
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

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