package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostSingleImageBinding
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.overflowmenu.view.OverflowMenuPopup
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE

class ItemPostSingleImageViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostSingleImageBinding, PostViewData>(),
    OverflowMenuAdapterListener {

    private lateinit var overflowMenu: OverflowMenuPopup

    override val viewType: Int
        get() = ITEM_POST_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): ItemPostSingleImageBinding {
        overflowMenu = OverflowMenuPopup.create(parent.context, this)

        return ItemPostSingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(binding: ItemPostSingleImageBinding, data: PostViewData, position: Int) {

        // sets items to overflow menu
        PostTypeUtil.setOverflowMenuItems(
            overflowMenu,
            data.menuItems
        )

        // sets data to the creator frame
        PostTypeUtil.initAuthorFrame(
            binding.authorFrame,
            data,
            overflowMenu
        )

        // sets the text content of the post
        PostTypeUtil.initTextContent(
            binding.tvPostContent,
            data,
            itemPosition = position,
            listener
        )

        // handles various actions for the post
        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data,
            listener
        )

        // loads post image and attaches listener
        PostTypeUtil.initPostSingleImage(
            binding.ivPost,
            data,
            listener
        )
    }

    // handles the menu item click on the post
    override fun onMenuItemClicked(menu: OverflowMenuItemViewData) {
        overflowMenu.dismiss()
        listener.onPostMenuItemClicked(menu.entityId, menu.title)
    }

}