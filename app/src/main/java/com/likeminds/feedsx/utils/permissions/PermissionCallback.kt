package com.likeminds.feedsx.utils.permissions

internal interface PermissionCallback {
    fun onGrant()
    fun onDeny()
}