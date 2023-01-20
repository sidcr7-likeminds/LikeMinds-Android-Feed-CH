package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostMultipleMediaBinding
import com.likeminds.feedsx.post.adapter.MultipleMediaPostAdapter
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.model.SampleViewType
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_VIDEO
import com.likeminds.feedsx.utils.model.ITEM_POST_MULTIPLE_MEDIA

class ItemPostMultipleMediaViewDataBinder :
    ViewDataBinder<ItemPostMultipleMediaBinding, PostViewData>() {

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
        //TODO: Change Implementation
        initViewPager(binding)
        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data
        )
        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

    private fun initViewPager(binding: ItemPostMultipleMediaBinding) {
        binding.viewpagerMultipleMedia.isSaveEnabled = false
        val multipleMediaPostAdapter = MultipleMediaPostAdapter()
        binding.viewpagerMultipleMedia.adapter = multipleMediaPostAdapter
        binding.dotsIndicator.setViewPager2(binding.viewpagerMultipleMedia)
        multipleMediaPostAdapter.add(SampleViewType(ITEM_MULTIPLE_MEDIA_IMAGE))
        multipleMediaPostAdapter.add(SampleViewType(ITEM_MULTIPLE_MEDIA_VIDEO))
    }

}