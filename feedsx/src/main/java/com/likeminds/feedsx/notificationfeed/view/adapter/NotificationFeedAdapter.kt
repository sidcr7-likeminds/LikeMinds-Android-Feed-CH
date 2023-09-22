package com.likeminds.feedsx.notificationfeed.view.adapter

import com.likeminds.feedsx.notificationfeed.model.ActivityViewData
import com.likeminds.feedsx.notificationfeed.view.adapter.databinder.LMFeedItemNotificationFeedViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class NotificationFeedAdapter constructor(
    val listener: NotificationFeedAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val lmFeedItemNotificationFeedViewDataBinder =
            LMFeedItemNotificationFeedViewDataBinder(listener)
        viewDataBinders.add(lmFeedItemNotificationFeedViewDataBinder)

        return viewDataBinders
    }

    interface NotificationFeedAdapterListener {
        fun onNotificationFeedItemClicked(position: Int, activityViewData: ActivityViewData)
    }
}