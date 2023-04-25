package com.likeminds.feedsample

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.likeminds.feedsample.FeedSXApplication.Companion.LOG_TAG
import com.likeminds.feedsample.pushnotification.LMFeedNotificationHandler

class FeedSXMessagingService : FirebaseMessagingService() {

    private lateinit var mNotificationHandler: LMFeedNotificationHandler

    override fun onCreate() {
        super.onCreate()
        mNotificationHandler = LMFeedNotificationHandler.getInstance()
        mNotificationHandler.create(this.application)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(LOG_TAG, "token generated: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(LOG_TAG, "message generated: ${message.data}")
        mNotificationHandler.handleNotification(message.data)
    }
}