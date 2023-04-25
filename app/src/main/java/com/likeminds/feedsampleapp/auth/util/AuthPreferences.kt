package com.likeminds.feedsampleapp.auth.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) : com.likeminds.feedsx.utils.sharedpreferences.BasePreferences(AUTH_PREFS, context) {

    companion object {
        const val AUTH_PREFS = "auth_prefs"
        const val API_KEY = "api_key"
        const val USER_NAME = "user_name"
        const val USER_ID = "user_id"
        const val IS_LOGGED_IN = "is_logged_in"
    }

    fun getApiKey(): String {
        return getPreference(API_KEY, "") ?: ""
    }

    fun saveApiKey(apiKey: String) {
        putPreference(API_KEY, apiKey)
    }

    fun getUserName(): String {
        return getPreference(USER_NAME, "") ?: ""
    }

    fun saveUserName(userName: String) {
        putPreference(USER_NAME, userName)
    }

    fun getUserId(): String {
        return getPreference(USER_ID, "") ?: ""
    }

    fun saveUserId(userId: String) {
        putPreference(USER_ID, userId)
    }

    fun getIsLoggedIn(): Boolean {
        return getPreference(IS_LOGGED_IN, false)
    }

    fun saveIsLoggedIn(isLoggedIn: Boolean) {
        putPreference(IS_LOGGED_IN, isLoggedIn)
    }

    fun clearPrefs() {
        saveApiKey("")
        saveUserName("")
        saveUserId("")
        saveIsLoggedIn(false)
    }
}