package com.likeminds.feedsx.post.create.util

import androidx.annotation.IntDef

const val CREATE_POST = 1
const val POST_DETAIL = 2
const val UNIVERSAL_FEED = 3


@IntDef(
    CREATE_POST,
    POST_DETAIL,
    UNIVERSAL_FEED
)
@Retention(AnnotationRetention.SOURCE)
annotation class PostScreen {
    companion object {
        fun isCreatePostFlow(@PostScreen screen: Int): Boolean {
            return screen == CREATE_POST
        }

        fun isUniversalFeedFlow(@PostScreen screen: Int): Boolean {
            return screen == UNIVERSAL_FEED
        }
    }
}
