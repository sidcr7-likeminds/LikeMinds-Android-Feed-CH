package com.likeminds.feedsx.media.view.adapter.databinder

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedItemMediaPickerSingleBinding
import com.likeminds.feedsx.media.model.MediaType
import com.likeminds.feedsx.media.model.MediaViewData
import com.likeminds.feedsx.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_SINGLE

class LMFeedMediaPickerSingleItemViewDataBinder constructor(
    private val listener: MediaPickerAdapterListener
) : ViewDataBinder<LmFeedItemMediaPickerSingleBinding, MediaViewData>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_SINGLE

    override fun createBinder(parent: ViewGroup): LmFeedItemMediaPickerSingleBinding {
        val binding = LmFeedItemMediaPickerSingleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener {
            val mediaViewData = binding.mediaViewData ?: return@setOnClickListener
            val position = binding.position ?: return@setOnClickListener
            if (listener.isMediaSelectionEnabled()) {
                listener.onMediaItemLongClicked(mediaViewData, position)
            } else {
                listener.onMediaItemClicked(mediaViewData, position)
            }
        }
        binding.root.setOnLongClickListener {
            if (listener.isMultiSelectionAllowed()) {
                val mediaViewData = binding.mediaViewData ?: return@setOnLongClickListener false
                val position = binding.position ?: return@setOnLongClickListener false
                listener.onMediaItemLongClicked(mediaViewData, position)
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }
        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(
        binding: LmFeedItemMediaPickerSingleBinding, data: MediaViewData, position: Int
    ) {
        binding.position = position
        binding.mediaViewData = data
        binding.isSelected = listener.isMediaSelected(data.uri.toString())

        glideRequestManager?.load(data.uri)
            ?.diskCacheStrategy(DiskCacheStrategy.NONE)
            ?.transition(DrawableTransitionOptions.withCrossFade())
            ?.placeholder(placeHolderDrawable)
            ?.error(placeHolderDrawable)
            ?.into(binding.ivThumbnail)

        val showVideoIcon = MediaType.isVideo(data.mediaType)
        binding.ivFileTypeIcon.isVisible = showVideoIcon
        binding.ivShadow.isVisible = showVideoIcon
    }
}