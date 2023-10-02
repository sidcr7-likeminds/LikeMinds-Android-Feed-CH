package com.likeminds.feedsx.topic.model

import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_ALL_TOPIC


class LMFeedAllTopicsViewData private constructor(
    val isSelected: Boolean
) : BaseViewType {
    override val viewType: Int
        get() = ITEM_ALL_TOPIC

    class Builder {

        private var isSelected: Boolean = true

        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }

        fun build() = LMFeedAllTopicsViewData(isSelected)
    }

    fun toBuilder(): Builder {
        return Builder().isSelected(isSelected)
    }
}