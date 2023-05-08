package com.likeminds.feedsx.media.customviews

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import com.likeminds.feedsx.R
import com.likeminds.feedsx.media.util.VideoCache

class LikeMindsVideoPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : StyledPlayerView(context, attrs, defStyleAttr) {

    private var videoSurfaceView: View? = null
    private lateinit var exoPlayer: ExoPlayer

    private var lastPos: Long = 0

    // creates an instance with DataSourceFactory for reading and writing cache
    private val cacheDataSourceFactory by lazy {
        CacheDataSource.Factory()
            .setCache(VideoCache.getInstance(context))
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory()
                    .setUserAgent(
                        Util.getUserAgent(
                            context, context.getString(
                                R.string.app_name
                            )
                        )
                    )
            )
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    init {
        if (isInEditMode) {
            videoSurfaceView = null
        } else {
            LayoutInflater.from(context).inflate(R.layout.exo_player_view, this, true)
            descendantFocusability = FOCUS_AFTER_DESCENDANTS

            // Content frame.
            videoSurfaceView = findViewById(R.id.surface_view)
            init()
        }
    }

    // initializes the exoplayer and sets player
    fun init() {
        reset()

        // used to configure ms of media to buffer before starting playback
        val defaultLoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                5000,
                10000,
                500,
                1500
            )
            .setAllocator(DefaultAllocator(true, 16))
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(defaultLoadControl)
            .build()

        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.playWhenReady = false
        exoPlayer.setVideoSurfaceView(videoSurfaceView as SurfaceView)

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    alpha = 1f
                }
            }
        })
        player = exoPlayer
    }

    // Prevents surface view to show black screen, will make it visible once video is loaded
    private fun reset() {
        alpha = 0f
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        videoSurfaceView?.visibility = visibility
    }

    /**
     * This will reuse the player and will play new URI (remote url) we have provided
     */
    fun startPlayingRemoteUri(videoUri: Uri) {
        val mediaSource =
            ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri))
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.seekTo(lastPos)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    /**
     * This will reuse the player and will play new URI (local uri) we have provided
     */
    fun startPlayingLocalUri(videoUri: Uri) {
        val mediaSource = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.seekTo(lastPos)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    /**
     * This will stop the player, but stopping the player shows black screen
     * so to cover that we set alpha to 0 of player
     * and lastFrame of player using imageView over player to make it look like paused player
     *
     * If we will not stop the player, only pause it, then it can cause memory issue due to overload of player
     * and paused player can not be played with new URL, after stopping the player we can reuse that with new URL
     *
     */
    fun removePlayer() {
        exoPlayer.playWhenReady = false
        lastPos = exoPlayer.currentPosition
        reset()
        exoPlayer.stop()
    }
}