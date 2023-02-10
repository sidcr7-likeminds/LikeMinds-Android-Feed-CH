package com.likeminds.feedsx.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemMediaPickerHeaderBinding
import com.likeminds.feedsx.media.model.MediaHeaderViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_HEADER

class MediaPickerHeaderItemViewDataBinder :
    ViewDataBinder<ItemMediaPickerHeaderBinding, MediaHeaderViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_HEADER

    override fun createBinder(parent: ViewGroup): ItemMediaPickerHeaderBinding {
        return ItemMediaPickerHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    }

    override fun bindData(
        binding: ItemMediaPickerHeaderBinding, data: MediaHeaderViewData, position: Int
    ) {
        binding.tvHeader.text = data.title
    }
}