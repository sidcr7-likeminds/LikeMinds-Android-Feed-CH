package com.likeminds.feedsampleapp.notificationfeed.view.adapter

import com.likeminds.feedsampleapp.notificationfeed.view.adapter.databinder.ItemNotificationFeedViewDataBinder
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

class NotificationFeedAdapter constructor(
    val listener: NotificationFeedAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val itemNotificationFeedViewDataBinder = ItemNotificationFeedViewDataBinder(listener)
        viewDataBinders.add(itemNotificationFeedViewDataBinder)

        return viewDataBinders
    }

    interface NotificationFeedAdapterListener {
        fun onPostMenuItemClicked(postId: String, title: String)
    }
}