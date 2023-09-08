package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostDocumentsBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS

class ItemPostDocumentsViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedItemPostDocumentsBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDocumentsBinding {
        return LmFeedItemPostDocumentsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostDocumentsBinding,
        data: PostViewData,
        position: Int
    ) {
        // handles various actions for the post
        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data,
            listener,
            position
        )

        // checks whether to bind complete data or not and execute corresponding lambda function
        PostTypeUtil.initPostTypeBindData(
            binding.authorFrame,
            binding.tvPostContent,
            data,
            position,
            listener,
            returnBinder = {
                return@initPostTypeBindData
            }, executeBinder = {
                // initializes the recycler view for documents attached
                PostTypeUtil.initDocumentsRecyclerView(
                    binding,
                    data,
                    listener,
                    position
                )
            })
    }
}