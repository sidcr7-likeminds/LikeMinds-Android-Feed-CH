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
import com.likeminds.feedsx.databinding.ItemPostSingleImageBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE

class PostSingleImageItemViewDataBinder :
    ViewDataBinder<ItemPostSingleImageBinding, BaseViewType>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_POST_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): ItemPostSingleImageBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostSingleImageBinding.inflate(inflater, parent, false)

        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return ItemPostSingleImageBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemPostSingleImageBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation
        initAuthor(binding)
        val data = "https://picsum.photos/id/237/200/300"
        glideRequestManager?.load(data)
            ?.diskCacheStrategy(DiskCacheStrategy.NONE)
            ?.transition(DrawableTransitionOptions.withCrossFade())
            ?.placeholder(placeHolderDrawable)
            ?.error(placeHolderDrawable)
            ?.into(binding.ivPost)

        val actionsBinding = binding.postActionsGrid
        actionsBinding.likesCount.text = "40 Likes"
        actionsBinding.commentsCount.text = "8 Comments"
        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

    private fun initAuthor(binding: ItemPostSingleImageBinding) {
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
        authorFrameBinding.tvTime.text = "7d"
    }

}