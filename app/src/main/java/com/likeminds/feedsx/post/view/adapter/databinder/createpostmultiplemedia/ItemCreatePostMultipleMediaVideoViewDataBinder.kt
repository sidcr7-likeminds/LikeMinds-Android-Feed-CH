package com.likeminds.feedsx.post.view.adapter.databinder.createpostmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemCreatePostSingleVideoBinding
import com.likeminds.feedsx.media.model.VIDEO
import com.likeminds.feedsx.post.util.CreatePostListener
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO

class ItemCreatePostMultipleMediaVideoViewDataBinder constructor(
    private val listener: CreatePostListener
) : ViewDataBinder<ItemCreatePostSingleVideoBinding, AttachmentViewData>() {
    override val viewType: Int
        get() = ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO

    override fun createBinder(parent: ViewGroup): ItemCreatePostSingleVideoBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemCreatePostSingleVideoBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemCreatePostSingleVideoBinding,
        data: AttachmentViewData,
        position: Int
    ) {
//        val video: Uri =
//            data.attachmentMeta.uri!!
//
//        binding.vvSingleVideoPost.setVideoURI(video)
//        binding.vvSingleVideoPost.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
//            mp.isLooping = true
//            binding.iconVideoPlay.hide()
//            binding.vvSingleVideoPost.start()
//        })

        binding.ivCross.setOnClickListener {
            listener.onMediaRemoved(position, VIDEO)
        }
    }
}