package com.likeminds.feedsx.feed.util

import com.likeminds.feedsx.posttypes.model.PostViewData

class PostPublisher {
    companion object {
        private var postPublisher: PostPublisher? = null

        @JvmStatic
        fun getPublisher(): PostPublisher {
            if (postPublisher == null) {
                postPublisher = PostPublisher()
            }
            return postPublisher!!
        }
    }

    // maintains the set of all the observers
    private var observers = hashSetOf<PostObserver>()

    // subscribes the observer to listen to the changes
    fun subscribe(postObserver: PostObserver) {
        observers.add(postObserver)
    }

    // unsubscribes the observer
    fun unsubscribe(postObserver: PostObserver) {
        observers.remove(postObserver)
    }

    // notifies all the observers with the new data
    fun notify(postData: Pair<String, PostViewData?>) {
        for (listener in observers) {
            listener.update(postData)
        }
    }

    interface PostObserver {
        // called whenever publisher notify the observer
        fun update(postData: Pair<String, PostViewData?>)
    }
}