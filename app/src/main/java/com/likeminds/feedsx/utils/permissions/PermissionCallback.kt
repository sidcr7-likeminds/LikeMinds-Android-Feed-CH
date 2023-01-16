package com.likeminds.feedsx.utils.permissions

interface PermissionCallback {
    fun onGrant()
    fun onDeny()
}