package com.likeminds.feedsx.utils.link

internal fun interface OnLinkClickListener {
    fun onLinkClicked(url: String): Boolean
}