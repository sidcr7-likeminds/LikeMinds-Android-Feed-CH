package com.likeminds.feedsx.utils

internal object SeeMoreUtil {

    /**
     * This function is for getting short post content for the see more feature
     */
    fun getShortContent(postContent: String?, seeMoreCountLimit: Int): String? {
        if (postContent == null)
            return null
        return if (postContent.length > seeMoreCountLimit) {
            postContent.substring(0, seeMoreCountLimit)
        } else {
            null
        }
    }
}