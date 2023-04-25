package com.likeminds.feedsampleapp.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.databinding.ItemMediaPickerDocumentBinding
import com.likeminds.feedsampleapp.media.model.MediaViewData
import com.likeminds.feedsampleapp.media.util.MediaPickerDataBinderUtils.Companion.getFilteredText
import com.likeminds.feedsampleapp.media.util.MediaUtils
import com.likeminds.feedsampleapp.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.feedsampleapp.utils.DateUtil
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.ITEM_MEDIA_PICKER_DOCUMENT

class MediaPickerDocumentItemViewDataBinder constructor(
    private val listener: MediaPickerAdapterListener
) : ViewDataBinder<ItemMediaPickerDocumentBinding, MediaViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_DOCUMENT

    override fun createBinder(parent: ViewGroup): ItemMediaPickerDocumentBinding {
        val binding = ItemMediaPickerDocumentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener {
            val mediaViewData = binding.mediaViewData ?: return@setOnClickListener
            val position = binding.position ?: return@setOnClickListener
            listener.onMediaItemClicked(mediaViewData, position)
        }
        return binding
    }

    override fun bindData(
        binding: ItemMediaPickerDocumentBinding, data: MediaViewData, position: Int
    ) {
        binding.position = position
        binding.mediaViewData = data
        binding.isSelected = listener.isMediaSelected(data.uri.toString())

        if (data.filteredKeywords.isNullOrEmpty()) {
            binding.tvDocumentName.text = data.mediaName
        } else {
            binding.tvDocumentName.setText(
                getFilteredText(
                    data.mediaName ?: "",
                    data.filteredKeywords.orEmpty(),
                    ContextCompat.getColor(binding.root.context, R.color.turquoise),
                ), TextView.BufferType.SPANNABLE
            )
        }

        binding.tvDocumentSize.text = MediaUtils.getFileSizeText(data.size)
        binding.tvDocumentDate.text = DateUtil.createDateFormat("dd/MM/yy", data.date)
    }
}