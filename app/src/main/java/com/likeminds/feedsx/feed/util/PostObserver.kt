package com.likeminds.feedsx.feed.util

import com.likeminds.feedsx.posttypes.model.PostViewData

interface PostChangesListener {
    fun update(postData: Pair<String, PostViewData?>)
}

class PostObserver {
    companion object {
        private var postPublisher: PostObserver? = null

        @JvmStatic
        fun getPublisher(): PostObserver {
            if (postPublisher == null) {
                postPublisher = PostObserver()
            }
            return postPublisher!!
        }
    }

    var listeners = hashSetOf<PostChangesListener>()

    fun subscribe(listener: PostChangesListener) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: PostChangesListener) {
        listeners.remove(listener)
    }

    fun notify(postData: Pair<String, PostViewData?>) {
        for (listener in listeners) {
            listener.update(postData)
        }
    }
}