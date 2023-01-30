package com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemMultipleMediaVideoBinding
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_VIDEO

class ItemCreatePostMultipleMediaVideoViewDataBinder :
    ViewDataBinder<ItemMultipleMediaVideoBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_MULTIPLE_MEDIA_VIDEO

    override fun createBinder(parent: ViewGroup): ItemMultipleMediaVideoBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemMultipleMediaVideoBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemMultipleMediaVideoBinding,
        data: BaseViewType,
        position: Int
    ) {
        //TODO: Testing data
        val video: Uri =
            Uri.parse("https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1")

        binding.videoPost.setVideoURI(video)
        binding.videoPost.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            mp.isLooping = true
            binding.iconVideoPlay.hide()
            binding.videoPost.start()
        })
    }
}