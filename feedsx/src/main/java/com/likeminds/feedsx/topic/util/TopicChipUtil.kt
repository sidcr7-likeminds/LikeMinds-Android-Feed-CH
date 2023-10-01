package com.likeminds.feedsx.topic.util

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.likeminds.feedsx.databinding.LmFeedEditTopicChipBinding
import com.likeminds.feedsx.databinding.LmFeedSelectTopicChipBinding
import com.likeminds.feedsx.databinding.LmFeedTopicChipBinding
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionExtras
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionActivity

object TopicChipUtil {

    //create select topic chip and add click listener and launch topic selection screen
    fun createSelectTopicsChip(
        context: Context,
        chipGroup: ChipGroup,
        startLauncher: (intent: Intent) -> Unit
    ): Chip {
        //create binding
        val binding = LmFeedSelectTopicChipBinding.inflate(
            LayoutInflater.from(chipGroup.context),
            chipGroup,
            false
        )
        val chip = binding.chipTopic

        //add click listener
        chip.setOnClickListener {
            val extras = LMFeedTopicSelectionExtras.Builder()
                .showAllTopicFilter(false)
                .showEnabledTopicOnly(true)
                .build()
            val intent = LMFeedTopicSelectionActivity.getIntent(context, extras)
            startLauncher(intent)
        }

        //return chip
        return chip
    }

    //create chips for selected topics
    fun createTopicChip(chipGroup: ChipGroup, topicName: String): Chip {
        val binding = LmFeedTopicChipBinding.inflate(
            LayoutInflater.from(chipGroup.context),
            chipGroup,
            false
        )
        binding.chipTopic.apply {
            text = topicName
            setEnsureMinTouchTargetSize(false)
        }
        return binding.chipTopic
    }

    //create edit chip and add click listener and launch topic selection screen
    fun createEditChip(
        context: Context,
        selectedTopic: List<LMFeedTopicViewData>,
        chipGroup: ChipGroup,
        disabledTopics: List<LMFeedTopicViewData>? = null,
        startLauncher: (intent: Intent) -> Unit
    ): Chip {
        val binding = LmFeedEditTopicChipBinding.inflate(
            LayoutInflater.from(chipGroup.context),
            chipGroup,
            false
        )
        val chip = binding.editChip
        chip.setEnsureMinTouchTargetSize(false)
        chip.setOnClickListener {
            val extras = LMFeedTopicSelectionExtras.Builder()
                .showAllTopicFilter(false)
                .selectedTopics(selectedTopic)
                .showEnabledTopicOnly(true)
                .disabledTopics(disabledTopics)
                .build()
            val intent = LMFeedTopicSelectionActivity.getIntent(context, extras)
            startLauncher(intent)
        }
        return chip
    }
}