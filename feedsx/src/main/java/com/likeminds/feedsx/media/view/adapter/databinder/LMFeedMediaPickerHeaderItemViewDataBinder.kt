package com.likeminds.feedsx.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemMediaPickerHeaderBinding
import com.likeminds.feedsx.media.model.MediaHeaderViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_HEADER

class LMFeedMediaPickerHeaderItemViewDataBinder :
    ViewDataBinder<LmFeedItemMediaPickerHeaderBinding, MediaHeaderViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_HEADER

    override fun createBinder(parent: ViewGroup): LmFeedItemMediaPickerHeaderBinding {
        return LmFeedItemMediaPickerHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    }

    override fun bindData(
        binding: LmFeedItemMediaPickerHeaderBinding, data: MediaHeaderViewData, position: Int
    ) {
        binding.tvHeader.text = data.title
    }
}