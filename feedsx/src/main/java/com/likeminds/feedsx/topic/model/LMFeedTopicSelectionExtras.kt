package com.likeminds.feedsx.topic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedTopicSelectionExtras private constructor(
    val showAllTopicFilter: Boolean,
    val selectedTopics: List<LMFeedTopicViewData>?,
    val showEnabledTopicOnly: Boolean,
) : Parcelable {

    class Builder {
        private var showAllTopicFilter: Boolean = false
        private var selectedTopics: List<LMFeedTopicViewData>? = null
        private var showEnabledTopicOnly: Boolean = false

        fun showAllTopicFilter(showAllTopicFilter: Boolean) =
            apply { this.showAllTopicFilter = showAllTopicFilter }

        fun selectedTopics(selectedTopics: List<LMFeedTopicViewData>?) =
            apply { this.selectedTopics = selectedTopics }

        fun showEnabledTopicOnly(showEnabledTopicOnly: Boolean) =
            apply { this.showEnabledTopicOnly = showEnabledTopicOnly }

        fun build() =
            LMFeedTopicSelectionExtras(
                showAllTopicFilter,
                selectedTopics,
                showEnabledTopicOnly
            )
    }

    fun toBuilder(): Builder {
        return Builder().showAllTopicFilter(showAllTopicFilter)
            .selectedTopics(selectedTopics)
            .showEnabledTopicOnly(showEnabledTopicOnly)
    }
}