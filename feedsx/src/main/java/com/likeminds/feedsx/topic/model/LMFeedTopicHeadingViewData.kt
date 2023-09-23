package com.likeminds.feedsx.topic.model

import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_TOPIC_HEADING

class LMFeedTopicHeadingViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_TOPIC_HEADING

    class Builder{
        fun build() = LMFeedTopicHeadingViewData()
    }
}