package com.likeminds.feedsx.posttypes.model

import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_SHIMMER

class PostShimmerViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_POST_SHIMMER

    class Builder {
        fun build() = PostShimmerViewData()
    }
}