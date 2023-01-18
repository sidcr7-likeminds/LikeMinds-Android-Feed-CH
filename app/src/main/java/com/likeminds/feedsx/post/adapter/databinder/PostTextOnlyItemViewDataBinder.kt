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
import com.likeminds.feedsx.databinding.ItemPostTextOnlyBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_TEXT_ONLY

class PostTextOnlyItemViewDataBinder :
    ViewDataBinder<ItemPostTextOnlyBinding, BaseViewType>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_POST_TEXT_ONLY

    override fun createBinder(parent: ViewGroup): ItemPostTextOnlyBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostTextOnlyBinding.inflate(inflater, parent, false)

        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(binding: ItemPostTextOnlyBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation
        initAuthor(binding)
        binding.tvPostContent.text = "Congrats to @talasha.sawant &amp; @kate92kt for being Community Hood CMs Of the Week! They both are founders of @_towardsabetteryou_  that is for mental &amp; emotional wellbeing ... See more"
        val actionsBinding = binding.postActionsGrid
        actionsBinding.likesCount.text = "30 Likes"
        actionsBinding.commentsCount.text = "2 Comments"
    }

    private fun initAuthor(binding: ItemPostTextOnlyBinding) {
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
        authorFrameBinding.tvTime.text = "10d"

    }

}