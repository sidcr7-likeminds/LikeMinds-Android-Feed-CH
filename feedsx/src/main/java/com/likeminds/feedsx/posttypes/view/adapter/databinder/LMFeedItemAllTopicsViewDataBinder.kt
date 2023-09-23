package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedLayoutAllTopicBinding
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.topic.model.LMFeedTopicHeadingViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_TOPIC_HEADING

class LMFeedItemAllTopicsViewDataBinder(
    private val listener: PostAdapterListener
) : ViewDataBinder<LmFeedLayoutAllTopicBinding, LMFeedTopicHeadingViewData>() {
    override val viewType: Int
        get() = ITEM_TOPIC_HEADING

    override fun createBinder(parent: ViewGroup): LmFeedLayoutAllTopicBinding {
        val binding = LmFeedLayoutAllTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        setListener(binding)

        return binding
    }

    override fun bindData(
        binding: LmFeedLayoutAllTopicBinding,
        data: LMFeedTopicHeadingViewData,
        position: Int
    ) {
    }

    private fun setListener(binding: LmFeedLayoutAllTopicBinding) {
        binding.root.setOnClickListener {
            listener.selectTopicsSelected()
        }
    }
}