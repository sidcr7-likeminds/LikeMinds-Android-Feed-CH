package com.likeminds.feedsx.media.util

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LMExoplayer @Inject constructor(@ApplicationContext private val context: Context) :
    Player.Listener {

    lateinit var exoplayer: ExoPlayer

    private lateinit var exoplayerListener: LMExoplayerListener
    private var positionOfItemInAdapter: Int = -1

    companion object {
        const val TAG = "PUI"
    }

    fun initialize(listener: LMExoplayerListener) {
        Log.d(TAG, "initialize ${LMExoplayer::class.java.name}")

        val defaultLoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(32 * 1024, 64 * 1024, 32 * 1024, 1024)
            .build()

        exoplayer = ExoPlayer.Builder(context)
            .setLoadControl(defaultLoadControl)
            .build()

        exoplayerListener = listener

        exoplayer.repeatMode = Player.REPEAT_MODE_ONE
        exoplayer.addListener(this)
    }

    fun setMediaItem(mediaItem: MediaItem) {
        Log.d("PUI", "setting item")
        clear()
        exoplayer.addMediaItem(mediaItem)
        Log.d("PUI", "prepare exo")
        exoplayer.prepare()
    }

    fun play() = exoplayer.play()

    fun pause() = exoplayer.pause()

    fun stop() = exoplayer.stop()

    fun release() = exoplayer.release()

    fun seekTo(whereTo: Long) = exoplayer.seekTo(whereTo)

    fun isPlaying() = exoplayer.isPlaying

    fun clear() = exoplayer.clearMediaItems()

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                Log.d(TAG, "buffer state")
            }
            Player.STATE_ENDED -> {
                Log.d(TAG, "ended state")
                exoplayerListener.videoEnded(positionOfItemInAdapter)
            }
            Player.STATE_IDLE -> {
                Log.d(TAG, "idle state")
            }
            Player.STATE_READY -> {
                Log.d(TAG, "ready state")
            }
        }
    }
}