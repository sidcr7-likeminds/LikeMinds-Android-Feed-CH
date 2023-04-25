package com.likeminds.feedsampleapp.utils.membertagging.util

import android.net.Uri

fun interface MemberTaggingDecoderListener {
    fun onTagClick(tag: Uri)
}