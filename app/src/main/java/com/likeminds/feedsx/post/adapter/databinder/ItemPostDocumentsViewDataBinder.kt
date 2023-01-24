package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostDocumentsBinding
import com.likeminds.feedsx.post.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.post.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.post.model.OverflowMenuItemViewData
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.util.PostTypeUtil
import com.likeminds.feedsx.post.view.OverflowMenuPopup
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS

class ItemPostDocumentsViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostDocumentsBinding, PostViewData>(),
    OverflowMenuAdapterListener {

    private lateinit var overflowMenu: OverflowMenuPopup

    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS

    override fun createBinder(parent: ViewGroup): ItemPostDocumentsBinding {
        overflowMenu = OverflowMenuPopup.create(parent.context, this)
        return ItemPostDocumentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(binding: ItemPostDocumentsBinding, data: PostViewData, position: Int) {
        overflowMenu.setItems(data.menuItems)

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