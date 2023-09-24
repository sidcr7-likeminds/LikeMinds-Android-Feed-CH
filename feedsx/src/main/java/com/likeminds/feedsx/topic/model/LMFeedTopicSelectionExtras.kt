package com.likeminds.feedsx.topic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedTopicSelectionExtras private constructor(
    val showAllTopicFilter: Boolean
) : Parcelable {

    class Builder {
        private var showAllTopicFilter: Boolean = false

        fun showAllTopicFilter(showAllTopicFilter: Boolean) =
            apply { this.showAllTopicFilter = showAllTopicFilter }

        fun build() = LMFeedTopicSelectionExtras(showAllTopicFilter)
    }

    fun toBuilder(): Builder {
        return Builder().showAllTopicFilter(showAllTopicFilter)
    }
}