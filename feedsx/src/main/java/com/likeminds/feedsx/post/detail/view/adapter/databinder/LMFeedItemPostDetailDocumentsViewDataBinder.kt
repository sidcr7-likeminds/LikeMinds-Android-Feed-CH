package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostDetailDocumentsBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS

class LMFeedItemPostDetailDocumentsViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedItemPostDetailDocumentsBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDetailDocumentsBinding {
        return LmFeedItemPostDetailDocumentsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostDetailDocumentsBinding,
        data: PostViewData,
        position: Int
    ) {
        binding.apply {
            // handles various actions for the post
            PostTypeUtil.initActionsLayout(
                postActionsLayout,
                data,
                listener,
                position
            )

            // checks whether to bind complete data or not and execute corresponding lambda function
            PostTypeUtil.initPostTypeBindData(
                authorFrame,
                tvPostTitle,
                tvPostContent,
                data,
                position,
                cgTopics,
                listener,
                returnBinder = {
                    return@initPostTypeBindData
                }, executeBinder = {
                    // initializes the recycler view for documents attached
                    PostTypeUtil.initDocumentsRecyclerView(
                        this,
                        data,
                        listener,
                        position
                    )
                })
        }
    }
}