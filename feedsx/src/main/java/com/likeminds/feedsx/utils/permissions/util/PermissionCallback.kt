package com.likeminds.feedsx.utils.permissions.util

interface PermissionCallback {
    fun onGrant()
    fun onDeny()
}