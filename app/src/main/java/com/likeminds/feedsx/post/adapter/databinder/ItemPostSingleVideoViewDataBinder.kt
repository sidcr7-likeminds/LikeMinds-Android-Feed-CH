package com.likeminds.feedsx.post.adapter.databinder

import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostSingleVideoBinding
import com.likeminds.feedsx.post.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO

class ItemPostSingleVideoViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostSingleVideoBinding, PostViewData>() {

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

    override fun bindData(binding: ItemPostSingleVideoBinding, data: PostViewData, position: Int) {
        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data
        )

        PostTypeUtil.initTextContent(
            binding.tvPostContent,
            data,
            itemPosition = position,
            listener
        )

        val video: Uri =
            Uri.parse(data.attachments.first().fileUrl)

        binding.videoPost.setVideoURI(video)
        binding.videoPost.setOnPreparedListener(OnPreparedListener { mp ->
            mp.isLooping = true
            binding.iconVideoPlay.hide()
            binding.videoPost.start()
        })
    }

}