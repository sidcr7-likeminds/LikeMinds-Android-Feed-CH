package com.likeminds.feedsx.utils.permissions

import android.app.Application
import com.likeminds.feedsx.utils.sharedpreferences.BasePreferences
import javax.inject.Inject

internal class SessionPermission @Inject constructor(application: Application) :
    BasePreferences(PERMISSION_PREFS, application) {
    companion object {
        const val PERMISSION_PREFS = "permission_prefs"
    }

    fun setPermissionRequest(permission: Permission) {
        putPreference(permission.permissionName, true)
    }

    fun wasPermissionRequestedBefore(permission: Permission): Boolean {
        return getPreference(permission.permissionName, false)
    }
}
