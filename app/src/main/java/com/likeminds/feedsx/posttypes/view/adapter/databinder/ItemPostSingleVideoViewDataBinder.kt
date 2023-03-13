package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostSingleVideoBinding
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.overflowmenu.view.OverflowMenuPopup
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO

class ItemPostSingleVideoViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostSingleVideoBinding, PostViewData>(),
    OverflowMenuAdapterListener {

    private lateinit var overflowMenu: OverflowMenuPopup

    override val viewType: Int
        get() = ITEM_POST_SINGLE_VIDEO

    override fun createBinder(parent: ViewGroup): ItemPostSingleVideoBinding {
        overflowMenu = OverflowMenuPopup.create(parent.context, this)
        return ItemPostSingleVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemPostSingleVideoBinding,
        data: PostViewData,
        position: Int
    ) {

        // handles various actions for the post
        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data,
            listener,
            position
        )

        // checks whether to bind complete data or not and execute corresponding lambda function
        PostTypeUtil.initPostTypeBindData(
            binding.authorFrame,
            overflowMenu,
            binding.tvPostContent,
            data,
            position,
            listener,
            returnBinder = {
                return@initPostTypeBindData
            }, executeBinder = {
                //TODO: Migrate to exo player
                val video: Uri =
                    Uri.parse(data.attachments.first().attachmentMeta.url)

                binding.videoPost.setVideoURI(video)
                binding.videoPost.setOnPreparedListener(OnPreparedListener { mp ->
                    mp.isLooping = true
                    binding.iconVideoPlay.hide()
                    binding.videoPost.start()
                })
            })
    }

    // handles the menu item click on the post
    override fun onMenuItemClicked(menu: OverflowMenuItemViewData) {
        overflowMenu.dismiss()
        listener.onPostMenuItemClicked(menu.entityId, menu.title)
    }

}