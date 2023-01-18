package com.likeminds.feedsx.post.adapter.databinder

import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostSingleVideoBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO

class PostSingleVideoItemViewDataBinder :
    ViewDataBinder<ItemPostSingleVideoBinding, BaseViewType>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_POST_SINGLE_VIDEO

    override fun createBinder(parent: ViewGroup): ItemPostSingleVideoBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostSingleVideoBinding.inflate(inflater, parent, false)

        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(binding: ItemPostSingleVideoBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation
        initAuthor(binding)
        val video: Uri =
            Uri.parse("https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1")

        binding.videoPost.setVideoURI(video)
        binding.videoPost.setOnPreparedListener(OnPreparedListener { mp ->
            mp.isLooping = true
            binding.iconVideoPlay.visibility = View.GONE
            binding.videoPost.start()
        })

        val actionsBinding = binding.postActionsGrid
        actionsBinding.likesCount.text = "70 Likes"
        actionsBinding.commentsCount.text = "5 Comments"
        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

    private fun initAuthor(binding: ItemPostSingleVideoBinding) {
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
        authorFrameBinding.tvTime.text = "9d"
    }

}