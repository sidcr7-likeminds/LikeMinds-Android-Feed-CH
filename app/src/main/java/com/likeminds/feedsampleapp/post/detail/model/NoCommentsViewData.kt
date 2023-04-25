package com.likeminds.feedsampleapp.post.detail.model

import com.likeminds.feedsampleapp.utils.model.BaseViewType
import com.likeminds.feedsampleapp.utils.model.ITEM_NO_COMMENTS_FOUND

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