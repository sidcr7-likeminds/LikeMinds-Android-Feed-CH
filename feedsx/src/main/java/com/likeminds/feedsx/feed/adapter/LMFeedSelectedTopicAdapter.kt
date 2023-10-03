package com.likeminds.feedsx.feed.adapter

import com.likeminds.feedsx.feed.adapter.databinder.LMFeedItemSelectedFilterTopicViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class LMFeedSelectedTopicAdapter(private val listener: LMFeedSelectedTopicAdapterListener) :
    BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): ArrayList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val lmFeedItemSelectedFilterTopicViewDataBinder =
            LMFeedItemSelectedFilterTopicViewDataBinder(listener)
        viewDataBinders.add(lmFeedItemSelectedFilterTopicViewDataBinder)

        return viewDataBinders
    }
}

interface LMFeedSelectedTopicAdapterListener {
    fun topicCleared(position: Int)
}