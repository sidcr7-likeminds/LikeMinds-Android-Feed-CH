package com.likeminds.feedsx.post.adapter.databinder.postmultiplemedia

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemMultipleMediaImageBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_IMAGE

class MultipleMediaImageItemViewDataBinder :
    ViewDataBinder<ItemMultipleMediaImageBinding, BaseViewType>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_MULTIPLE_MEDIA_IMAGE

    override fun createBinder(parent: ViewGroup): ItemMultipleMediaImageBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMultipleMediaImageBinding.inflate(inflater, parent, false)
        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(
        binding: ItemMultipleMediaImageBinding,
        data: BaseViewType,
        position: Int
    ) {
        //TODO: Change implementation
        val data = "https://picsum.photos/id/237/200/300"
        glideRequestManager?.load(data)
            ?.diskCacheStrategy(DiskCacheStrategy.NONE)
            ?.transition(DrawableTransitionOptions.withCrossFade())
            ?.placeholder(placeHolderDrawable)
            ?.error(placeHolderDrawable)
            ?.into(binding.ivPost)
    }
}