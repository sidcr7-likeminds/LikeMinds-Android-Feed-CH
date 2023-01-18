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
import com.likeminds.feedsx.databinding.ItemPostLinkBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_LINK

class PostLinkItemViewDataBinder :
    ViewDataBinder<ItemPostLinkBinding, BaseViewType>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): ItemPostLinkBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostLinkBinding.inflate(inflater, parent, false)

        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(binding: ItemPostLinkBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation
        initAuthor(binding)
        binding.tvLinkDescription.text =
            "The new feature has already been implemented in the United States and Other Eu…"
        binding.tvLinkTitle.text = "Twitter will soon let you schedule your tweets"
        binding.tvLinkUrl.text = "www.youtube.com"
        val actionsBinding = binding.postActionsGrid
        actionsBinding.likesCount.text = "24 Likes"
        actionsBinding.commentsCount.text = "3 Comments"
        binding.tvPostContent.text =
            "https://www.livemint.com/technology/tech-news/twitter-will-soon-let-you-schedule-your-tweets-11588997453592.html"
    }

    private fun initAuthor(binding: ItemPostLinkBinding) {
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
        authorFrameBinding.tvTime.text = "4d"
    }

}