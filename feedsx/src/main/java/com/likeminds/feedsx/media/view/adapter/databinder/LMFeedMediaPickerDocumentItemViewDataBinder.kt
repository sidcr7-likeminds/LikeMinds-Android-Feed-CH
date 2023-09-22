package com.likeminds.feedsx.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedItemMediaPickerDocumentBinding
import com.likeminds.feedsx.media.model.MediaViewData
import com.likeminds.feedsx.media.util.MediaPickerDataBinderUtils.Companion.getFilteredText
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.feedsx.utils.DateUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_DOCUMENT

class LMFeedMediaPickerDocumentItemViewDataBinder constructor(
    private val listener: MediaPickerAdapterListener
) : ViewDataBinder<LmFeedItemMediaPickerDocumentBinding, MediaViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_DOCUMENT

    override fun createBinder(parent: ViewGroup): LmFeedItemMediaPickerDocumentBinding {
        val binding = LmFeedItemMediaPickerDocumentBinding.inflate(
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
        binding: LmFeedItemMediaPickerDocumentBinding, data: MediaViewData, position: Int
    ) {
        binding.apply {
            this.position = position
            mediaViewData = data
            isSelected = listener.isMediaSelected(data.uri.toString())

            if (data.filteredKeywords.isNullOrEmpty()) {
                tvDocumentName.text = data.mediaName
            } else {
                tvDocumentName.setText(
                    getFilteredText(
                        data.mediaName ?: "",
                        data.filteredKeywords,
                        ContextCompat.getColor(binding.root.context, R.color.turquoise),
                    ), TextView.BufferType.SPANNABLE
                )
            }

            tvDocumentSize.text = MediaUtils.getFileSizeText(data.size)
            tvDocumentDate.text = DateUtil.createDateFormat("dd/MM/yy", data.date)
        }
    }
}