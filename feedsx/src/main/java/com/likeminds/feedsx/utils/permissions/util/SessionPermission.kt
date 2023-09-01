package com.likeminds.feedsx.utils.permissions.util

import android.app.Application
import com.likeminds.feedsx.utils.sharedpreferences.BasePreferences
import javax.inject.Inject

class SessionPermission @Inject constructor(application: Application) :
    BasePreferences(PERMISSION_PREFS, application) {
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
