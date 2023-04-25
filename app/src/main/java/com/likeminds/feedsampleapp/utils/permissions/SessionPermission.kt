package com.likeminds.feedsampleapp.utils.permissions

import android.content.Context
import com.likeminds.feedsampleapp.utils.sharedpreferences.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionPermission @Inject constructor(@ApplicationContext private val context: Context) :
    BasePreferences(PERMISSION_PREFS, context) {
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
