package com.likeminds.feedsx.utils.permissions

internal interface PermissionDeniedCallback {
    fun onDeny()
    fun onCancel()
}
