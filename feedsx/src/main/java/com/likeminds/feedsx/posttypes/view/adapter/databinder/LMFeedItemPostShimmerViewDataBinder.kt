package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostShimmerBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_SHIMMER

class LMFeedItemPostShimmerViewDataBinder :
    ViewDataBinder<LmFeedItemPostShimmerBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_POST_SHIMMER

    override fun createBinder(parent: ViewGroup): LmFeedItemPostShimmerBinding {
        return LmFeedItemPostShimmerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostShimmerBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}