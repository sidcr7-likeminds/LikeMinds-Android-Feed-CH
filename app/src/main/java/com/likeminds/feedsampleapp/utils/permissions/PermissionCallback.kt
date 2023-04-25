package com.likeminds.feedsampleapp.utils.permissions

interface PermissionCallback {
    fun onGrant()
    fun onDeny()
}