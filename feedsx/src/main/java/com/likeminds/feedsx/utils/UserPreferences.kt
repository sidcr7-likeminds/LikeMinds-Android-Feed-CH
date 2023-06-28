package com.likeminds.feedsx.utils

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.likeminds.feedsx.utils.sharedpreferences.BasePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val application: Application
) : BasePreferences(USER_PREFS, application) {

    companion object {
        const val USER_PREFS = "user_prefs"
        const val API_KEY = "api_key"
        const val USER_NAME = "user_name"
        const val USER_UNIQUE_ID = "user_unique_id"
        const val IS_GUEST = "is_guest"
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

    fun getUserUniqueId(): String {
        return getPreference(USER_UNIQUE_ID, "") ?: ""
    }

    fun saveUserUniqueId(userUniqueId: String) {
        putPreference(USER_UNIQUE_ID, userUniqueId)
    }

    fun getIsGuest(): Boolean {
        return getPreference(IS_GUEST, false)
    }

    fun saveIsGuest(isGuest: Boolean) {
        putPreference(IS_GUEST, isGuest)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }

    fun clearPrefs() {
        saveApiKey("")
        saveUserName("")
        saveUserUniqueId("")
        saveIsGuest(false)
    }
}