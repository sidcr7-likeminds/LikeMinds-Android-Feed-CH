package com.likeminds.feedsampleapp.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsampleapp.databinding.ItemMediaPickerBrowseBinding
import com.likeminds.feedsampleapp.media.model.MediaBrowserViewData
import com.likeminds.feedsampleapp.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.ITEM_MEDIA_PICKER_BROWSE

class MediaPickerBrowseItemViewDataBinder constructor(
    private val listener: MediaPickerAdapterListener
) : ViewDataBinder<ItemMediaPickerBrowseBinding, MediaBrowserViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_BROWSE

    override fun createBinder(parent: ViewGroup): ItemMediaPickerBrowseBinding {
        val binding = ItemMediaPickerBrowseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener {
            listener.browseDocumentClicked()
        }
        return binding
    }

    override fun bindData(
        binding: ItemMediaPickerBrowseBinding, data: MediaBrowserViewData, position: Int
    ) {
    }
}