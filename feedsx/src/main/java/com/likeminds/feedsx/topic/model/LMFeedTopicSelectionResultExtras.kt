package com.likeminds.feedsx.topic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedTopicSelectionResultExtras private constructor(
    val isAllTopicSelected: Boolean,
    val selectedTopics: List<LMFeedTopicViewData>
) : Parcelable {

    class Builder {
        private var isAllTopicSelected: Boolean = false
        private var selectedTopics: List<LMFeedTopicViewData> = emptyList()

        fun isAllTopicSelected(isAllTopicSelected: Boolean) =
            apply { this.isAllTopicSelected = isAllTopicSelected }

        fun selectedTopics(selectedTopics: List<LMFeedTopicViewData>) =
            apply { this.selectedTopics = selectedTopics }

        fun build() = LMFeedTopicSelectionResultExtras(isAllTopicSelected, selectedTopics)
    }

    fun toBuilder(): Builder {
        return Builder().selectedTopics(selectedTopics)
            .isAllTopicSelected(isAllTopicSelected)
    }
}