package com.likeminds.feedsx.feed.util

import com.likeminds.feedsx.posttypes.model.PostViewData

// to trigger post change events and notify observers
class PostEvent {
    companion object {
        private var postEvent: PostEvent? = null

        @JvmStatic
        fun getPublisher(): PostEvent {
            if (postEvent == null) {
                postEvent = PostEvent()
            }
            return postEvent!!
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
        /*
        * called whenever post changes are notified the observer
        * postData - Pair of postId and Post data
        * */
        fun update(postData: Pair<String, PostViewData?>)
    }
}