package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemNoCommentsFoundBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_NO_COMMENTS_FOUND

class ItemNoCommentsFoundViewDataBinder :
    ViewDataBinder<LmFeedItemNoCommentsFoundBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_NO_COMMENTS_FOUND

    override fun createBinder(parent: ViewGroup): LmFeedItemNoCommentsFoundBinding {
        return LmFeedItemNoCommentsFoundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemNoCommentsFoundBinding,
        data: BaseViewType,
        position: Int
    ) {
        //showing static data
    }
}