package com.likeminds.feedsx.media.util

import android.net.Uri
import com.likeminds.feedsx.media.customviews.LikeMindsVideoPlayerView
import javax.inject.Singleton

@Singleton
class DraftVideoAutoPlayHelper {
    companion object {
        private var draftVideoAutoPlayHelper: DraftVideoAutoPlayHelper? = null

        fun getInstance(): DraftVideoAutoPlayHelper {
            if (draftVideoAutoPlayHelper == null) {
                draftVideoAutoPlayHelper = DraftVideoAutoPlayHelper()
            }
            return draftVideoAutoPlayHelper!!
        }
    }

    private var lastPlayerView: LikeMindsVideoPlayerView? = null

    fun logic(videoPost: LikeMindsVideoPlayerView, uri: Uri? = null, url: String? = null) {
        if (uri == null && url == null) {
            return
        }
        if (lastPlayerView == null || lastPlayerView != videoPost) {
            if (uri != null) {
                videoPost.startPlayingLocal(uri)
            } else {
                val updatedUri = Uri.parse(url)
                videoPost.startPlaying(updatedUri)
            }
            // stop last player
            removePlayer()
        }
        lastPlayerView = videoPost
    }

    fun removePlayer() {
        lastPlayerView?.resetPlayer()
        lastPlayerView = null
    }
}