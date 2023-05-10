package com.likeminds.feedsx.utils.permissions

interface PermissionDeniedCallback {
    fun onDeny()
    fun onCancel()
}
