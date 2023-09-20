package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedPostDetailArticleBinding
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_ARTICLE

class LMFeedItemPostDetailArticleViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<LmFeedPostDetailArticleBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_ARTICLE

    override fun createBinder(parent: ViewGroup): LmFeedPostDetailArticleBinding {
        return LmFeedPostDetailArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedPostDetailArticleBinding,
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