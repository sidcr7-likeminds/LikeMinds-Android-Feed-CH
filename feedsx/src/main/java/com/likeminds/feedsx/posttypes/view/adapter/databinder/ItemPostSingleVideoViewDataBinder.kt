package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostSingleVideoBinding
import com.likeminds.feedsx.media.model.MEDIA_ACTION_NONE
import com.likeminds.feedsx.media.model.MEDIA_ACTION_PAUSE
import com.likeminds.feedsx.media.model.MEDIA_ACTION_PLAY
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO

class ItemPostSingleVideoViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemPostSingleVideoBinding, PostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_VIDEO

    override fun createBinder(parent: ViewGroup): ItemPostSingleVideoBinding {
        val binding = ItemPostSingleVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

//        binding.iconVideoPlay.setOnClickListener {
//            val position = binding.position ?: return@setOnClickListener
//            listener.playPauseOnVideo(position)
//        }

        return binding
    }

    override fun bindData(
        binding: ItemPostSingleVideoBinding,
        data: PostViewData,
        position: Int
    ) {
        binding.position = position

        // handles various actions for the post
        PostTypeUtil.initActionsLayout(
            binding.postActionsLayout,
            data,
            listener,
            position
        )

        val attachment = data.attachments.first()
        when (attachment.mediaActions) {
            MEDIA_ACTION_NONE -> {
//                binding.iconVideoPlay.setImageResource(R.drawable.ic_play)
            }
            MEDIA_ACTION_PLAY -> {
//                binding.iconVideoPlay.setImageResource(R.drawable.ic_pause)
            }
            MEDIA_ACTION_PAUSE -> {
//                binding.iconVideoPlay.setImageResource(R.drawable.ic_play)
            }
        }

        // checks whether to bind complete data or not and execute corresponding lambda function
        PostTypeUtil.initPostTypeBindData(
            binding.authorFrame,
            binding.tvPostContent,
            data,
            position,
            listener,
            returnBinder = {
                return@initPostTypeBindData
            }, executeBinder = {
                val videoUri = Uri.parse(data.attachments.first().attachmentMeta.url)
                binding.videoPost.reset()
                // Set separate ID for each player view, to prevent it being overlapped by other player's changes
//                binding.videoPost.id = View.generateViewId()

//                listener.sendMediaItemToExoPlayer(position, binding.videoPost, mediaItem)
//                binding.videoPost.setVideoURI(video)
//                binding.videoPost.setOnPreparedListener(OnPreparedListener { mp ->
//                    mp.isLooping = true
//                    binding.iconVideoPlay.hide()
//                    binding.videoPost.start()
//                })
            })
    }
}