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
import com.likeminds.feedsx.databinding.ItemPostDocumentsBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS

class PostDocumentsItemViewDataBinder :
    ViewDataBinder<ItemPostDocumentsBinding, BaseViewType>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS

    override fun createBinder(parent: ViewGroup): ItemPostDocumentsBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostDocumentsBinding.inflate(inflater, parent, false)
        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(binding: ItemPostDocumentsBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation
        initAuthor(binding)
        val actionsBinding = binding.postActionsGrid
        actionsBinding.likesCount.text = "20 Likes"
        actionsBinding.commentsCount.text = "4 Comments"
        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

    private fun initAuthor(binding: ItemPostDocumentsBinding) {
        val data = "https://pics.freeicons.io/uploads/icons/png/5722348461605810028-512.png"
        val authorFrameBinding = binding.authorFrame
        authorFrameBinding.tvMemberName.text = "Siddharth"
        authorFrameBinding.tvTime.text = "3d"
        authorFrameBinding.tvCustomTitle.text = "Admin"
        glideRequestManager?.load(data)
            ?.diskCacheStrategy(DiskCacheStrategy.NONE)
            ?.transition(DrawableTransitionOptions.withCrossFade())
            ?.placeholder(placeHolderDrawable)
            ?.error(placeHolderDrawable)
            ?.into(authorFrameBinding.memberImage)
    }

}