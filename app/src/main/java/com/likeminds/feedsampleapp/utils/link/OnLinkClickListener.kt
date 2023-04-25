package com.likeminds.feedsampleapp.utils.link

internal fun interface OnLinkClickListener {
    fun onLinkClicked(url: String): Boolean
}