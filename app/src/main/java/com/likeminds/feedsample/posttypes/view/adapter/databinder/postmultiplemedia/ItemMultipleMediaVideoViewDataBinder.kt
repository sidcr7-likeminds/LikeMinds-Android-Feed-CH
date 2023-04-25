package com.likeminds.feedsample.posttypes.view.adapter.databinder.postmultiplemedia

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemMultipleMediaVideoBinding
import com.likeminds.feedsample.posttypes.model.AttachmentViewData
import com.likeminds.feedsample.utils.ViewUtils.hide
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType
import com.likeminds.feedsample.utils.model.ITEM_MULTIPLE_MEDIA_VIDEO

class ItemMultipleMediaVideoViewDataBinder :
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
        //TODO: Migrate to exo player
        val attachment = data as AttachmentViewData

        val video: Uri =
            Uri.parse(attachment.attachmentMeta.url)

        binding.videoPost.setVideoURI(video)
        binding.videoPost.setOnPreparedListener { mp ->
            mp.isLooping = true
            binding.iconVideoPlay.hide()
            binding.videoPost.start()
        }
    }
}