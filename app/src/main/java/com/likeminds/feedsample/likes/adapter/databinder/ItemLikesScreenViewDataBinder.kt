package com.likeminds.feedsample.likes.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemLikesScreenBinding
import com.likeminds.feedsample.likes.model.LikeViewData
import com.likeminds.feedsample.utils.MemberImageUtil
import com.likeminds.feedsample.utils.ViewUtils.hide
import com.likeminds.feedsample.utils.ViewUtils.show
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.ITEM_LIKES_SCREEN

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