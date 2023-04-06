package com.likeminds.feedsx.posttypes.view.adapter.databinder

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.likeminds.feedsx.databinding.ItemPostSingleVideoBinding
import com.likeminds.feedsx.media.util.LMExoplayer
import com.likeminds.feedsx.media.util.LMExoplayerListener
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO

class ItemPostSingleVideoViewDataBinder constructor(
    val listener: PostAdapterListener,
    private val isPostDetail: Boolean
) : ViewDataBinder<ItemPostSingleVideoBinding, PostViewData>(), LMExoplayerListener {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_VIDEO

    private var lmExoplayer = listener.getLMExoPlayer()

    override fun createBinder(parent: ViewGroup): ItemPostSingleVideoBinding {
        val binding = ItemPostSingleVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.iconVideoPlay.setOnClickListener {
            val position = binding.position ?: return@setOnClickListener
            listener.playPauseOnVideo(position)
        }

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
                if (!isPostDetail) return@initPostTypeBindData
                val attachment = data.attachments.first()
                releasePlayer()
                val playerView = binding.videoPost
                initializePlayer(playerView, Uri.parse(attachment.attachmentMeta.url))
            })
    }

    private fun initializePlayer(playerView: StyledPlayerView, uri: Uri) {
        if (lmExoplayer == null) {
            lmExoplayer = LMExoplayer(playerView.context, true, Player.REPEAT_MODE_ONE, this)
            listener.setLMExoPlayer(lmExoplayer)
            playerView.player = lmExoplayer?.exoPlayer
            playerView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING)
        }
        lmExoplayer?.setMediaItem(uri)
    }

    private fun releasePlayer() {
        lmExoplayer?.release()
        lmExoplayer = null
        listener.setLMExoPlayer(null)
    }
}