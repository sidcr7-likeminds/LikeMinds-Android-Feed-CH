package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostTextOnlyBinding
import com.likeminds.feedsx.post.adapter.OverflowMenuAdapter
import com.likeminds.feedsx.post.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.post.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.post.model.OverflowMenuItemViewData
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.post.view.OverflowMenuPopup
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_TEXT_ONLY

class ItemPostTextOnlyViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostTextOnlyBinding, PostViewData>(),
    OverflowMenuAdapterListener {

    private lateinit var overflowMenu: OverflowMenuPopup

    override val viewType: Int
        get() = ITEM_POST_TEXT_ONLY

    override fun createBinder(parent: ViewGroup): ItemPostTextOnlyBinding {
        overflowMenu = OverflowMenuPopup.create(parent.context, this)
        return ItemPostTextOnlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(binding: ItemPostTextOnlyBinding, data: PostViewData, position: Int) {
        //TODO: Testing data
        val list = listOf(OverflowMenuItemViewData.Builder().title("Edit").build(), OverflowMenuItemViewData.Builder().title("Delete").build())
        overflowMenu.setItems(list)

        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data,
            overflowMenu
        )

        PostTypeUtil.initTextContent(
            binding.tvPostContent,
            data,
            itemPosition = position,
            listener
        )

        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data
        )
    }

    override fun onMenuItemClicked(menu: OverflowMenuItemViewData) {
        listener.onPostMenuItemClicked(postId, menu.title)
    }

}