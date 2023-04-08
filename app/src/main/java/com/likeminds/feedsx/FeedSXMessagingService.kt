package com.likeminds.feedsx

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.likeminds.feedsx.pushnotification.LMFeedNotificationHandler

class FeedSXMessagingService : FirebaseMessagingService() {

    private lateinit var mNotificationHandler: LMFeedNotificationHandler

    override fun onCreate() {
        super.onCreate()
        mNotificationHandler = LMFeedNotificationHandler.getInstance()
        mNotificationHandler.create(this.application)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("LikeMinds", "token generated: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("LikeMinds", "message generated: ${message.data}")
        mNotificationHandler.handleNotification(message.data)
    }
}