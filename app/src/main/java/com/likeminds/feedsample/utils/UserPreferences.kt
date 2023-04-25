package com.likeminds.feedsample.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.likeminds.feedsample.utils.sharedpreferences.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) : BasePreferences(USER_PREFS, context) {

    companion object {
        const val USER_PREFS = "user_prefs"
        const val USER_UNIQUE_ID = "user_unique_id"
    }

    fun getUserUniqueId(): String {
        return getPreference(USER_UNIQUE_ID, "") ?: ""
    }

    fun saveUserUniqueId(memberId: String) {
        putPreference(USER_UNIQUE_ID, memberId)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }
}