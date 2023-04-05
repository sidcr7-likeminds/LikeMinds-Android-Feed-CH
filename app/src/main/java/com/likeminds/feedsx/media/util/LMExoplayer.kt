package com.likeminds.feedsx.media.util

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class LMExoplayer(
    private val context: Context,
    private val playWhenReady: Boolean,
    private val repeatMode: Int
) {

    lateinit var exoPlayer: ExoPlayer

    init {
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        exoPlayer = ExoPlayer.Builder(context).setLoadControl(
            DefaultLoadControl.Builder()
                .setBufferDurationsMs(32 * 1024, 64 * 1024, 32 * 1024, 1024)
                .build()
        ).build()
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.repeatMode = repeatMode
    }


    fun start() = exoPlayer.play()

    fun pause() = exoPlayer.pause()

    fun stop() = exoPlayer.stop()

    fun release() = exoPlayer.release()

    fun seekTo(whereTo: Long) = exoPlayer.seekTo(whereTo)

    fun isPlaying() = exoPlayer.isPlaying

    fun clear() = exoPlayer.clearMediaItems()

    fun setMediaItem(uri: Uri) {
        val item = MediaItem.fromUri(uri)
        clear()
        exoPlayer.addMediaItem(item)
        exoPlayer.prepare()
    }
}