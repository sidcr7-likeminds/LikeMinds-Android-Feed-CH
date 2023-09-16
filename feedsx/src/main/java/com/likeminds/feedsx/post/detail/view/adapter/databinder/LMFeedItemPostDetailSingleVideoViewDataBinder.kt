package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostDetailSingleVideoBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO

class LMFeedItemPostDetailSingleVideoViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedItemPostDetailSingleVideoBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_VIDEO

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDetailSingleVideoBinding {
        return LmFeedItemPostDetailSingleVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostDetailSingleVideoBinding,
        data: PostViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position

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
                listener,
                returnBinder = {
                    return@initPostTypeBindData
                }, executeBinder = {
                    videoPost.setOnClickListener {
                        listener.postDetail(data.id)
                    }
                })
        }
    }
}