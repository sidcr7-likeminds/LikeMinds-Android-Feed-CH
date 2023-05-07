package com.likeminds.feedsx.media.customviews

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util
import com.likeminds.feedsx.R
import com.likeminds.feedsx.media.util.VideoCache

class LikeMindsVideoPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var videoSurfaceView: View?
    private var player: Player? = null
    private var isTouching = false

    private var lastPos: Long = 0

    private val cacheDataSourceFactory = CacheDataSource.Factory()
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

        val defaultLoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(3000, 8000, 500, 1500)
            .setAllocator(DefaultAllocator(true, 16))
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(defaultLoadControl)
            .build()

        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    alpha = 1f
                }
            }
        })

        exoPlayer.playWhenReady = false
        setPlayer(exoPlayer)
    }


    // Prevents surface view to show black screen, will make it visible once video is loaded
    fun reset() {
        alpha = 0f
    }

    /**
     * Set the [Player] to use.
     * @param player The [Player] to use, or `null` to detach the current player.
     * Only players which are accessed on the main thread are supported
     * (`player.getApplicationLooper() == Looper.getMainLooper()`).
     */
    private fun setPlayer(player: Player?) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper())
        Assertions.checkArgument(
            player == null || player.applicationLooper == Looper.getMainLooper()
        )
        if (this.player === player) {
            return
        }
        val oldPlayer = this.player
        oldPlayer?.clearVideoSurfaceView(videoSurfaceView as SurfaceView)
        this.player = player
        player?.setVideoSurfaceView(videoSurfaceView as SurfaceView)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        videoSurfaceView?.visibility = visibility
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (player != null) {
            return super.dispatchKeyEvent(event)
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (player == null) {
            false
        } else when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
                true
            }
            MotionEvent.ACTION_UP -> {
                if (isTouching) {
                    isTouching = false
                    performClick()
                    return true
                }
                false
            }
            else -> false
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }

    /**
     * This will reuse the player and will play new URI we have provided
     */
    fun startPlaying(videoUri: Uri) {
        val mediaSource =
            ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri))
        (player as ExoPlayer).setMediaSource(mediaSource)
        player?.seekTo(lastPos)
        player?.playWhenReady = true
        (player as ExoPlayer).prepare()
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
        player?.playWhenReady = false
        lastPos = player?.currentPosition ?: 0
        reset()
        player?.stop()
    }
}