package com.likeminds.feedsample.post.detail.model

import com.likeminds.feedsample.utils.model.BaseViewType
import com.likeminds.feedsample.utils.model.ITEM_NO_COMMENTS_FOUND

class NoCommentsViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_NO_COMMENTS_FOUND

    class Builder {
        fun build() = NoCommentsViewData()
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}