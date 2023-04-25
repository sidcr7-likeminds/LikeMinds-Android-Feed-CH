package com.likeminds.feedsample.utils.link

internal fun interface OnLinkClickListener {
    fun onLinkClicked(url: String): Boolean
}