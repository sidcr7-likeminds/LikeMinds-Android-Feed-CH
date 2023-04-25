package com.likeminds.feedsample.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemMediaPickerHeaderBinding
import com.likeminds.feedsample.media.model.MediaHeaderViewData
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.ITEM_MEDIA_PICKER_HEADER

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