package com.likeminds.feedsx.likes.adapter

import com.likeminds.feedsx.likes.adapter.databinder.ItemLikesScreenViewDataBinder
import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

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