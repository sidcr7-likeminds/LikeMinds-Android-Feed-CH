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
        const val USER_UNIQUE_ID = "user_unique_id"
        const val USER_UUID = "user_uuid"
    }

    fun getUserUniqueId(): String {
        return getPreference(USER_UNIQUE_ID, "") ?: ""
    }

    fun saveUserUniqueId(memberId: String) {
        putPreference(USER_UNIQUE_ID, memberId)
    }

    fun getUUID(): String {
        return getPreference(USER_UUID, "") ?: ""
    }

    fun saveUUID(uuid: String) {
        putPreference(USER_UUID, uuid)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }
}