package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostMultipleMediaBinding
import com.likeminds.feedsx.media.util.LMExoplayer
import com.likeminds.feedsx.post.create.util.VideoPlayerPageChangeListener
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_MULTIPLE_MEDIA

class ItemPostMultipleMediaViewDataBinder(
    val listener: PostAdapterListener,
    private val flow: Int
) : ViewDataBinder<ItemPostMultipleMediaBinding, PostViewData>(), VideoPlayerPageChangeListener {

    override val viewType: Int
        get() = ITEM_POST_MULTIPLE_MEDIA

    override fun createBinder(parent: ViewGroup): ItemPostMultipleMediaBinding {
        return ItemPostMultipleMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemPostMultipleMediaBinding,
        data: PostViewData,
        position: Int
    ) {
        Log.d("PUI", "bind data called")
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
                PostTypeUtil.initViewPager(binding, data, flow, this)
            })
    }

    override fun getLMExoPlayer(): LMExoplayer? {
        return listener.getLMExoPlayer()
    }

    override fun setLMExoPlayer(lmExoplayer: LMExoplayer?) {
        listener.setLMExoPlayer(lmExoplayer)
    }
}