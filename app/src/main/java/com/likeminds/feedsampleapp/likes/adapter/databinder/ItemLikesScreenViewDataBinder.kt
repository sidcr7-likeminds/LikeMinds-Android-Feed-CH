package com.likeminds.feedsampleapp.likes.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsampleapp.databinding.ItemLikesScreenBinding
import com.likeminds.feedsampleapp.likes.model.LikeViewData
import com.likeminds.feedsampleapp.utils.MemberImageUtil
import com.likeminds.feedsampleapp.utils.ViewUtils.hide
import com.likeminds.feedsampleapp.utils.ViewUtils.show
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.ITEM_LIKES_SCREEN

class ItemLikesScreenViewDataBinder : ViewDataBinder<ItemLikesScreenBinding, LikeViewData>() {

    override val viewType: Int
        get() = ITEM_LIKES_SCREEN

    override fun createBinder(parent: ViewGroup): ItemLikesScreenBinding {
        return ItemLikesScreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(binding: ItemLikesScreenBinding, data: LikeViewData, position: Int) {
        initLikeItem(binding, data)
    }

    private fun initLikeItem(
        binding: ItemLikesScreenBinding,
        data: LikeViewData
    ) {
        val user = data.user
        MemberImageUtil.setImage(
            user.imageUrl,
            user.name,
            data.id,
            binding.memberImage,
            showRoundImage = true
        )

        binding.apply {
            tvMemberName.text = user.name
            if (!user.customTitle.isNullOrEmpty()) {
                viewDot.show()
                tvCustomTitle.show()
                tvCustomTitle.text = user.customTitle
            } else {
                viewDot.hide()
                tvCustomTitle.hide()
            }
        }
        binding.tvMemberName.text = user.name

    }
}