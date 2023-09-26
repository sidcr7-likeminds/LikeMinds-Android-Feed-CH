package com.likeminds.feedsx

import org.json.JSONObject

interface LMFeedUICallback {
    fun login() {
        //to implement whenever refresh token is expired
    }

    fun updateNotificationCount(count: Int) {
        //to implement whenever notification count is updated
    }

    fun openProfile(
        uuid: String,
        userId: String,
        source: String
    ) {
        //to implement whenever profile is opened from feed
    }

    fun trackAnalytics(
        eventKey: String,
        jsonObject: JSONObject
    ) {
        //to implement whenever analytics event is triggered
    }
}