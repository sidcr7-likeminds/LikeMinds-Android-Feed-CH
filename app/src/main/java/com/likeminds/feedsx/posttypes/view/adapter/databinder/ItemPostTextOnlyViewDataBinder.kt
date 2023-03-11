package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostTextOnlyBinding
import com.likeminds.feedsx.overflowmenu.model.DELETE_POST_MENU_ITEM
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.overflowmenu.model.REPORT_POST_MENU_ITEM
import com.likeminds.feedsx.overflowmenu.view.OverflowMenuPopup
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
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

    override fun bindData(
        binding: ItemPostTextOnlyBinding,
        data: PostViewData,
        position: Int
    ) {
        //TODO: Testing data
        val list = listOf(
            OverflowMenuItemViewData.Builder().title(DELETE_POST_MENU_ITEM).entityId(data.id)
                .build(),
            OverflowMenuItemViewData.Builder().title(REPORT_POST_MENU_ITEM).entityId(data.id)
                .build()
        )

        // handles various actions for the post
        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data,
            listener
        )

        if (data.fromPostLiked || data.fromPostSaved) {
            return
        } else {
            // sets items to overflow menu
            PostTypeUtil.setOverflowMenuItems(
                overflowMenu,
                list
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
        }
    }

    // handles the menu item click on the post
    override fun onMenuItemClicked(menu: OverflowMenuItemViewData) {
        overflowMenu.dismiss()
        listener.onPostMenuItemClicked(menu.entityId, menu.title)
    }

}