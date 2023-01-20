package com.likeminds.feedsx.post.util

import android.view.View
import com.likeminds.feedsx.databinding.LayoutAuthorFrameBinding
import com.likeminds.feedsx.databinding.LayoutPostActionsBinding
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.model.UserViewData
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.TimeUtil

object PostTypeUtil {

    // TODO: Change Data

    fun initAuthorFrame(
        binding: LayoutAuthorFrameBinding,
        data: PostViewData,
    ) {
        // creator data
        val user: UserViewData = data.user
        binding.tvMemberName.text = user.name
        binding.tvCustomTitle.text = user.customTitle
        MemberImageUtil.setImage(
            user.imageUrl,
            user.name,
            data.id,
            binding.memberImage,
            showRoundImage = true
        )

        // edited post
        if (data.isEdited) {
            binding.viewDotEdited.visibility = View.VISIBLE
            binding.tvEdited.visibility = View.VISIBLE
            binding.tvTime.text = TimeUtil.getDaysHoursOrMinutes(data.updatedAt)
        } else {
            binding.viewDotEdited.visibility = View.GONE
            binding.tvEdited.visibility = View.GONE
            binding.tvTime.text = TimeUtil.getDaysHoursOrMinutes(data.createdAt)
        }
    }

    fun initActionsLayout(
        binding: LayoutPostActionsBinding,
        data: PostViewData,
    ) {
        binding.likesCount.text = data.likesCount.toString()
        binding.commentsCount.text = data.commentsCount.toString()
    }

}