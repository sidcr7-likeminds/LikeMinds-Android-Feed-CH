package com.likeminds.feedsx.feed.util

import com.likeminds.feedsx.databinding.ItemLikesScreenBinding
import com.likeminds.feedsx.feed.view.model.LikesViewData
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.ViewUtils.show

object LikesScreenUtil {

    fun initLikeItem(
        binding: ItemLikesScreenBinding,
        data: LikesViewData
    ) {
        val user = data.user
        MemberImageUtil.setImage(
            user.imageUrl,
            user.name,
            data.id,
            binding.memberImage,
            showRoundImage = true
        )

        binding.tvMemberName.text = user.name
        if (!user.customTitle.isNullOrEmpty()) {
            binding.viewDot.show()
            binding.tvCustomTitle.text = user.customTitle
        }
    }
}