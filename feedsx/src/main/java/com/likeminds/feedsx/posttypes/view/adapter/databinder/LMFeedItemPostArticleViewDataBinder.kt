package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemPostArticleBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_ARTICLE

class LMFeedItemPostArticleViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedItemPostArticleBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_ARTICLE

    override fun createBinder(parent: ViewGroup): LmFeedItemPostArticleBinding {
        return LmFeedItemPostArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostArticleBinding,
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
                data,
                position,
                listener,
                returnBinder = {
                    return@initPostTypeBindData
                }, executeBinder = {
                    // loads post image and attaches listener
                    PostTypeUtil.initPostArticle(
                        binding.ivArticle,
                        data,
                        listener
                    )
                })
        }
    }
}