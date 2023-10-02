package com.likeminds.feedsx.topic.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.likeminds.feedsx.databinding.LmFeedItemAllTopicBinding
import com.likeminds.feedsx.topic.adapter.LMFeedTopicSelectionAdapterListener
import com.likeminds.feedsx.topic.model.LMFeedAllTopicsViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_ALL_TOPIC

class LMFeedAllTopicsViewDataBinder(private val listener: LMFeedTopicSelectionAdapterListener) :
    ViewDataBinder<LmFeedItemAllTopicBinding, LMFeedAllTopicsViewData>() {
    override val viewType: Int
        get() = ITEM_ALL_TOPIC

    override fun createBinder(parent: ViewGroup): LmFeedItemAllTopicBinding {
        val binding = LmFeedItemAllTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        setListeners(binding)

        return binding
    }

    override fun bindData(
        binding: LmFeedItemAllTopicBinding,
        data: LMFeedAllTopicsViewData,
        position: Int
    ) {
        binding.apply {
            //set values in binding
            lmFeedAllTopic = data
            this.position = position

            //set selected click
            ivSelected.isVisible = data.isSelected
        }
    }

    private fun setListeners(binding: LmFeedItemAllTopicBinding) {
        binding.root.setOnClickListener {
            val lmFeedAllTopic = binding.lmFeedAllTopic ?: return@setOnClickListener
            val position = binding.position
            listener.allTopicSelected(lmFeedAllTopic, position)
        }
    }
}