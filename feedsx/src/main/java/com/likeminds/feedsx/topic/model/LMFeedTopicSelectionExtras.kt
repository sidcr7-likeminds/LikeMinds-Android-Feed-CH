package com.likeminds.feedsx.topic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedTopicSelectionExtras private constructor(
    val showAllTopicFilter: Boolean,
    val selectedTopics: List<LMFeedTopicViewData>?,
    val showEnabledTopicOnly: Boolean,
    val disabledTopics: List<LMFeedTopicViewData>?
) : Parcelable {

    class Builder {
        private var showAllTopicFilter: Boolean = false
        private var selectedTopics: List<LMFeedTopicViewData>? = null
        private var showEnabledTopicOnly: Boolean = false
        private var disabledTopics: List<LMFeedTopicViewData>? = null

        fun showAllTopicFilter(showAllTopicFilter: Boolean) =
            apply { this.showAllTopicFilter = showAllTopicFilter }

        fun selectedTopics(selectedTopics: List<LMFeedTopicViewData>?) =
            apply { this.selectedTopics = selectedTopics }

        fun showEnabledTopicOnly(showEnabledTopicOnly: Boolean) =
            apply { this.showEnabledTopicOnly = showEnabledTopicOnly }

        fun disabledTopics(disabledTopics: List<LMFeedTopicViewData>?) =
            apply { this.disabledTopics = disabledTopics }

        fun build() =
            LMFeedTopicSelectionExtras(
                showAllTopicFilter,
                selectedTopics,
                showEnabledTopicOnly,
                disabledTopics
            )
    }

    fun toBuilder(): Builder {
        return Builder().showAllTopicFilter(showAllTopicFilter)
            .selectedTopics(selectedTopics)
            .showEnabledTopicOnly(showEnabledTopicOnly)
            .disabledTopics(disabledTopics)
    }
}