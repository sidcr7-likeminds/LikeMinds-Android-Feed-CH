package com.likeminds.feedsample.utils.permissions

interface PermissionCallback {
    fun onGrant()
    fun onDeny()
}