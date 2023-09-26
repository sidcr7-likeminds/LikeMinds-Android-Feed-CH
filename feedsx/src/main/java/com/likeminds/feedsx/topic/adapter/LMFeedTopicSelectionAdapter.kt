package com.likeminds.feedsx.topic.adapter

import com.likeminds.feedsx.topic.adapter.databinders.LMFeedAllTopicsViewDataBinder
import com.likeminds.feedsx.topic.adapter.databinders.LMFeedTopicViewDataBinder
import com.likeminds.feedsx.topic.model.LMFeedAllTopicsViewData
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class LMFeedTopicSelectionAdapter(private val listener: LMFeedTopicSelectionAdapterListener) :
    BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): ArrayList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val lmFeedTopicViewDataBinder = LMFeedTopicViewDataBinder(listener)
        viewDataBinders.add(lmFeedTopicViewDataBinder)

        val lmFeedAllTopicsViewDataBinder = LMFeedAllTopicsViewDataBinder(listener)
        viewDataBinders.add(lmFeedAllTopicsViewDataBinder)

        return viewDataBinders
    }
}

interface LMFeedTopicSelectionAdapterListener {
    fun allTopicSelected(lmFeedAllTopic: LMFeedAllTopicsViewData, position: Int)
    fun topicSelected(topic: LMFeedTopicViewData, position: Int)
}