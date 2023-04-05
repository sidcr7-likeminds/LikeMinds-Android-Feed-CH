package com.likeminds.feedsx.post.create.util

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.likeminds.feedsx.R
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.media.util.LMExoplayer

class VideoPlayerPageChangeCallback(
    private val selectedMediaUri: List<SingleUriData>,
    private val viewPager2: ViewPager2,
    private var lmExoplayer: LMExoplayer?
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
        if (lmExoplayer == null) {
            lmExoplayer = LMExoplayer(playerView.context, false, Player.REPEAT_MODE_OFF)
            playerView.player = lmExoplayer?.exoPlayer
        }
        lmExoplayer?.setMediaItem(uri)
    }

    private fun releasePlayer() {
        lmExoplayer?.release()
        lmExoplayer = null
    }
}