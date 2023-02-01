package com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemCreatePostSingleVideoBinding
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_MULTIPLE_MEDIA_VIDEO

class ItemCreatePostMultipleMediaVideoViewDataBinder :
    ViewDataBinder<ItemCreatePostSingleVideoBinding, AttachmentViewData>() {
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
        val video: Uri =
            data.attachmentMeta.uri!!

        binding.vvSingleVideoPost.setVideoURI(video)
        binding.vvSingleVideoPost.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            mp.isLooping = true
            binding.iconVideoPlay.hide()
            binding.vvSingleVideoPost.start()
        })
        binding.ivCross.setOnClickListener {
            //TODO:
        }
    }
}