package com.likeminds.feedsample.utils.membertagging.view.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.likeminds.feedsample.databinding.ItemMemberBinding
import com.likeminds.feedsample.utils.membertagging.model.UserTagViewData

internal class MemberViewHolder(
    val binding: ItemMemberBinding,
    val darkMode: Boolean,
    private val memberAdapterClickListener: MemberAdapterClickListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            val member = binding.member ?: return@setOnClickListener
            memberAdapterClickListener.onMemberTagged(member)
        }
    }

    @JvmSynthetic
    internal fun bind(userAndGroup: UserTagViewData) {
        binding.member = userAndGroup
        binding.hideBottomLine = userAndGroup.isLastItem && !darkMode
        binding.darkMode = darkMode

        //set description and hide in case of description is empty
        binding.tvDescription.apply {
            isVisible = userAndGroup.description.isNotEmpty()
            text = userAndGroup.description
        }

        Glide.with(binding.ivMemberImage)
            .load(userAndGroup.imageUrl)
            .placeholder(userAndGroup.placeholder)
            .error(userAndGroup.placeholder)
            .into(binding.ivMemberImage)
        binding.executePendingBindings()
    }

}