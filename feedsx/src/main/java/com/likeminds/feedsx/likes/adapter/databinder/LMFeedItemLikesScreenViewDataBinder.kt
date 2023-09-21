package com.likeminds.feedsx.likes.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.LikeMindsFeedUI
import com.likeminds.feedsx.databinding.LmFeedItemLikesScreenBinding
import com.likeminds.feedsx.likes.model.LikeViewData
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_LIKES_SCREEN

class LMFeedItemLikesScreenViewDataBinder :
    ViewDataBinder<LmFeedItemLikesScreenBinding, LikeViewData>() {

    override val viewType: Int
        get() = ITEM_LIKES_SCREEN

    override fun createBinder(parent: ViewGroup): LmFeedItemLikesScreenBinding {
        return LmFeedItemLikesScreenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemLikesScreenBinding,
        data: LikeViewData,
        position: Int
    ) {
        initLikeItem(binding, data)
    }

    private fun initLikeItem(
        binding: LmFeedItemLikesScreenBinding,
        data: LikeViewData
    ) {
        val user = data.user
        MemberImageUtil.setImage(
            user.imageUrl,
            user.name,
            user.sdkClientInfoViewData.uuid,
            binding.memberImage,
            showRoundImage = true
        )

        binding.apply {
            // calls a callback to navigate to profile screen
            root.setOnClickListener {
                LikeMindsFeedUI.lmFeedListener.openProfile(
                    user.sdkClientInfoViewData.uuid,
                    user.id.toString(),
                    LMFeedAnalytics.Source.FEED
                )
            }

            tvMemberName.text = user.name
            if (!user.customTitle.isNullOrEmpty()) {
                viewDot.show()
                tvCustomTitle.show()
                tvCustomTitle.text = user.customTitle
            } else {
                viewDot.hide()
                tvCustomTitle.hide()
            }
            tvMemberName.text = user.name
        }
    }
}