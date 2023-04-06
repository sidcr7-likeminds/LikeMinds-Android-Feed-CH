package com.likeminds.feedsx.post.create.util

import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.likeminds.feedsx.R
import com.likeminds.feedsx.media.util.LMExoplayer
import com.likeminds.feedsx.media.util.LMExoplayerListener
import com.likeminds.feedsx.posttypes.model.AttachmentViewData

class VideoPlayerPageChangeCallback(
    private var selectedMediaUri: List<AttachmentViewData>,
    private val viewPager2: ViewPager2,
    private val isCreatePostFlow: Boolean,
    private val playWhenReady: Boolean,
    private val repeatMode: Int,
    private val listener: VideoPlayerPageChangeListener
) : ViewPager2.OnPageChangeCallback(), LMExoplayerListener {

    private var count = 0

    private var lmExoplayer = listener.getLMExoPlayer()

    fun setList(attachments: List<AttachmentViewData>) {
        selectedMediaUri = attachments
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        releasePlayer()
        val playerView = getCurrentPlayerView() ?: return
        val attachmentMeta = selectedMediaUri[position].attachmentMeta
        val uri = if (isCreatePostFlow) {
            attachmentMeta.uri
        } else {
            Uri.parse(attachmentMeta.url)
        } ?: return
        initializePlayer(playerView, uri)
    }

    private fun getCurrentPlayerView(): StyledPlayerView? {
        val view = (viewPager2.getChildAt(0) as? RecyclerView)?.findViewHolderForAdapterPosition(
            viewPager2.currentItem
        )
        return if (isCreatePostFlow) {
            view?.itemView?.findViewById(R.id.vv_single_video_post)
        } else {
            view?.itemView?.findViewById(R.id.video_post)
        }
    }

    private fun initializePlayer(playerView: StyledPlayerView, uri: Uri) {
        if (lmExoplayer == null) {
            lmExoplayer = LMExoplayer(playerView.context, playWhenReady, repeatMode, this)
            Log.d("PUI", "count: $count")
            count++
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