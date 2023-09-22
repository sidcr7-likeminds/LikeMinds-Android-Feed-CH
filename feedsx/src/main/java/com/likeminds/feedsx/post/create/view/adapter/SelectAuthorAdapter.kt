package com.likeminds.feedsx.post.create.view.adapter

import com.likeminds.feedsx.post.create.view.adapter.databinder.LMFeedItemSelectAuthorViewDataBinder
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class SelectAuthorAdapter constructor(
    private val listener: SelectAuthorAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val lmFeedItemSelectAuthorViewDataBinder = LMFeedItemSelectAuthorViewDataBinder(listener)
        viewDataBinders.add(lmFeedItemSelectAuthorViewDataBinder)

        return viewDataBinders
    }
}

interface SelectAuthorAdapterListener {
    fun onUserSelected(userViewData: UserViewData?)
}