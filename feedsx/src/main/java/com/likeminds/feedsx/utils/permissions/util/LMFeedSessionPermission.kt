package com.likeminds.feedsx.utils.permissions.util

import android.app.Application
import com.likeminds.feedsx.utils.sharedpreferences.LMFeedBasePreferences
import javax.inject.Inject

class LMFeedSessionPermission @Inject constructor(application: Application) :
    LMFeedBasePreferences(PERMISSION_PREFS, application) {
    companion object {
        const val PERMISSION_PREFS = "permission_prefs"
    }

    fun setPermissionRequest(permissionName: String) {
        putPreference(permissionName, true)
    }

    fun wasPermissionRequestedBefore(permissionName: String): Boolean {
        return getPreference(permissionName, false)
    }
}
