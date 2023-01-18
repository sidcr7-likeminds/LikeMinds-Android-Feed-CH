package com.likeminds.feedsx.post.adapter.databinder

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SampleViewType
import com.likeminds.feedsx.databinding.ItemPostMultipleMediaBinding
import com.likeminds.feedsx.post.adapter.MultipleMediaPostAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_VIDEO
import com.likeminds.feedsx.utils.model.ITEM_POST_MULTIPLE_MEDIA

class PostMultipleMediaItemViewDataBinder :
    ViewDataBinder<ItemPostMultipleMediaBinding, BaseViewType>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_POST_MULTIPLE_MEDIA

    override fun createBinder(parent: ViewGroup): ItemPostMultipleMediaBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostMultipleMediaBinding.inflate(inflater, parent, false)

        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(
        binding: ItemPostMultipleMediaBinding,
        data: BaseViewType,
        position: Int
    ) {
        //TODO: Change Implementation
        initViewPager(binding)
        initAuthor(binding)
        val actionsBinding = binding.postActionsGrid
        actionsBinding.likesCount.text = "30 Likes"
        actionsBinding.commentsCount.text = "6 Comments"
        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

    private fun initAuthor(binding: ItemPostMultipleMediaBinding) {
        val data = "https://pics.freeicons.io/uploads/icons/png/5722348461605810028-512.png"
        val authorFrameBinding = binding.authorFrame
        authorFrameBinding.tvMemberName.text = "Siddharth"
        authorFrameBinding.tvCustomTitle.text = "Admin"
        glideRequestManager?.load(data)
            ?.diskCacheStrategy(DiskCacheStrategy.NONE)
            ?.transition(DrawableTransitionOptions.withCrossFade())
            ?.placeholder(placeHolderDrawable)
            ?.error(placeHolderDrawable)
            ?.into(authorFrameBinding.memberImage)
        authorFrameBinding.tvTime.text = "5d"
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