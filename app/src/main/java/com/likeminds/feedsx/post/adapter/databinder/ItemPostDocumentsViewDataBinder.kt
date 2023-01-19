package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostDocumentsBinding
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS

class ItemPostDocumentsViewDataBinder :
    ViewDataBinder<ItemPostDocumentsBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS

    override fun createBinder(parent: ViewGroup): ItemPostDocumentsBinding {
        return ItemPostDocumentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(binding: ItemPostDocumentsBinding, data: BaseViewType, position: Int) {
        //TODO: Change Implementation

        PostTypeUtil.initAuthor(
            binding.authorFrame,
            "https://pics.freeicons.io/uploads/icons/png/5722348461605810028-512.png"
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            "https://pics.freeicons.io/uploads/icons/png/5722348461605810028-512.png"
        )

        binding.tvPostContent.text = "Let’s welcome our new joinees to this community."
    }

}