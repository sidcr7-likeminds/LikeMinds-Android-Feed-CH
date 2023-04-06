package com.likeminds.feedsx.media.util

interface LMExoplayerListener {
    fun videoEnded() {}
    fun videoBuffer() {}
    fun videoReady() {}
}