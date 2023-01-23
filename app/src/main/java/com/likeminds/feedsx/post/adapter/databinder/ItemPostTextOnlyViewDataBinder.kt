package com.likeminds.feedsx.post.adapter.databinder

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostTextOnlyBinding
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_TEXT_ONLY

class ItemPostTextOnlyViewDataBinder :
    ViewDataBinder<ItemPostTextOnlyBinding, PostViewData>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_POST_TEXT_ONLY

    override fun createBinder(parent: ViewGroup): ItemPostTextOnlyBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostTextOnlyBinding.inflate(inflater, parent, false)

        glideRequestManager = Glide.with(binding.root)
        placeHolderDrawable =
            ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        return binding
    }

    override fun bindData(binding: ItemPostTextOnlyBinding, data: PostViewData, position: Int) {
        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data
        )

        //TODO: Testing data
        binding.tvPostContent.text =
            "Congrats to @talasha.sawant &amp; @kate92kt for being Community Hood CMs Of the Week! They both are founders of @_towardsabetteryou_  that is for mental &amp; emotional wellbeing ... See more"
    }

}