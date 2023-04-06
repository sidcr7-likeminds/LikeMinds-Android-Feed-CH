package com.likeminds.feedsx.post.create.util

import com.likeminds.feedsx.media.util.LMExoplayer

interface VideoPlayerPageChangeListener {
    fun getLMExoPlayer(): LMExoplayer?
    fun setLMExoPlayer(lmExoplayer: LMExoplayer?)
}