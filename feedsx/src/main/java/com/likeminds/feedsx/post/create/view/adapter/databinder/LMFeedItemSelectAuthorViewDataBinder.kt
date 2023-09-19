package com.likeminds.feedsx.post.create.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.LmFeedItemSelectAuthorBinding
import com.likeminds.feedsx.post.create.view.adapter.SelectAuthorAdapterListener
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_USER

class LMFeedItemSelectAuthorViewDataBinder constructor(
    private val listener: SelectAuthorAdapterListener,
) : ViewDataBinder<LmFeedItemSelectAuthorBinding, UserViewData>() {

    override val viewType: Int
        get() = ITEM_USER

    override fun createBinder(parent: ViewGroup): LmFeedItemSelectAuthorBinding {
        val binding = LmFeedItemSelectAuthorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setListeners(binding)
        return binding
    }

    private fun setListeners(binding: LmFeedItemSelectAuthorBinding) {
        binding.root.setOnClickListener {
            listener.onUserSelected(binding.userViewData)
        }
    }

    override fun bindData(
        binding: LmFeedItemSelectAuthorBinding,
        data: UserViewData,
        position: Int
    ) {
        binding.apply {
            userViewData = data
            tvMemberName.text = data.name
            MemberImageUtil.setImage(
                data.imageUrl,
                data.name,
                data.sdkClientInfoViewData.uuid,
                memberImage,
                showRoundImage = true
            )
        }
    }
}