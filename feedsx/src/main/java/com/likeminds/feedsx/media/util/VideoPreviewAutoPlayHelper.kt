package com.likeminds.feedsx.media.util

import android.net.Uri
import android.widget.ProgressBar
import com.likeminds.feedsx.media.customviews.LMFeedVideoPlayerView
import javax.inject.Singleton

@Singleton
class VideoPreviewAutoPlayHelper {
    companion object {
        private var videoPreviewAutoPlayHelper: VideoPreviewAutoPlayHelper? = null

        fun getInstance(): VideoPreviewAutoPlayHelper {
            if (videoPreviewAutoPlayHelper == null) {
                videoPreviewAutoPlayHelper = VideoPreviewAutoPlayHelper()
            }
            return videoPreviewAutoPlayHelper!!
        }
    }

    private var lastPlayerView: LMFeedVideoPlayerView? = null

    /**
     * @param [videoPost] - Player view in which the provided video is played
     * @param [uri] - If the video is local, then provided [uri] is used to play locally
     * @param [url] - If the video is remote, then provided [url] is used to play locally
     */
    fun playVideo(
        videoPost: LMFeedVideoPlayerView,
        progressBar: ProgressBar,
        uri: Uri? = null,
        url: String? = null
    ) {
        if (uri == null && url == null) {
            return
        }
        if (lastPlayerView == null || lastPlayerView != videoPost) {
            if (uri != null) {
                videoPost.startPlayingLocalUri(uri, progressBar)
            } else {
                val updatedUri = Uri.parse(url)
                videoPost.startPlayingRemoteUri(updatedUri, progressBar)
            }
            // stop last player
            removePlayer()
        }
        lastPlayerView = videoPost
    }

    fun removePlayer() {
        if (lastPlayerView != null) {
            // stop last player
            lastPlayerView?.removePlayer()
            lastPlayerView = null
        }
    }
}