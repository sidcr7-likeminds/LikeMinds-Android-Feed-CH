package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostSingleImageBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE

class ItemPostSingleImageViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedItemPostSingleImageBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): LmFeedItemPostSingleImageBinding {
        return LmFeedItemPostSingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostSingleImageBinding,
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
                // loads post image and attaches listener
                PostTypeUtil.initPostSingleImage(
                    binding.ivPost,
                    data,
                    listener
                )
            })
    }
}