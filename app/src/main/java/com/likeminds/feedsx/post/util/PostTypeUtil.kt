package com.likeminds.feedsx.post.util

import com.likeminds.feedsx.databinding.LayoutAuthorFrameBinding
import com.likeminds.feedsx.databinding.LayoutPostActionsBinding
import com.likeminds.feedsx.utils.MemberImageUtil

object PostTypeUtil {

    // TODO: Change Data

    fun initAuthor(
        binding: LayoutAuthorFrameBinding,
        data: String?,
    ) {
        binding.tvMemberName.text = "Siddharth"
        binding.tvTime.text = "3d"
        binding.tvCustomTitle.text = "Admin"
        MemberImageUtil.setImage(
            data,
            "Siddharth",
            null,
            binding.memberImage,
            showRoundImage = true
        )
    }

    fun initActionsLayout(
        binding: LayoutPostActionsBinding,
        data: String?,
    ) {
        binding.likesCount.text = "20 Likes"
        binding.commentsCount.text = "4 Comments"
    }

}