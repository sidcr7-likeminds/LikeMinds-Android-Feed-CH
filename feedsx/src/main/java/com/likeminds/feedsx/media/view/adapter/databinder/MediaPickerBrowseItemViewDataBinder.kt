package com.likeminds.feedsx.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemMediaPickerBrowseBinding
import com.likeminds.feedsx.media.model.MediaBrowserViewData
import com.likeminds.feedsx.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_BROWSE

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
        //showing static data
    }
}