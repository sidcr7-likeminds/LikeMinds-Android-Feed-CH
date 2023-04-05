package com.likeminds.feedsx.post.create.util

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.likeminds.feedsx.R
import com.likeminds.feedsx.media.model.SingleUriData

class VideoPlayerPageChangeCallback(
    private val selectedMediaUri: List<SingleUriData>,
    private val viewPager2: ViewPager2,
    private var exoPlayer: ExoPlayer?
) :
    ViewPager2.OnPageChangeCallback() {

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        releasePlayer()
        val playerView = getCurrentPlayerView() ?: return
        initializePlayer(playerView, selectedMediaUri[position].uri)
    }

    private fun getCurrentPlayerView(): StyledPlayerView? {
        return (viewPager2.getChildAt(0) as? RecyclerView)?.findViewHolderForAdapterPosition(
            viewPager2.currentItem
        )?.itemView?.findViewById(R.id.vv_single_video_post)
    }

    private fun initializePlayer(playerView: StyledPlayerView, uri: Uri) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(playerView.context).build()
            playerView.player = exoPlayer
            exoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
            exoPlayer?.playWhenReady = false
        }
        exoPlayer?.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer?.prepare()
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}