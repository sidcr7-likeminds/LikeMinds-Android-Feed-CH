package com.likeminds.feedsx.utils.permissions

import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity

class PermissionManager {

    companion object {
        fun performTaskWithPermission(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            task: PermissionTask,
            permission: Permission,
            showInitialPopup: Boolean,
            showDeniedPopup: Boolean,
            setInitialPopupDismissible: Boolean = false,
            setDeniedPopupDismissible: Boolean = false,
            permissionDeniedCallback: PermissionDeniedCallback? = null,
        ) {
            if (activity.hasPermission(permission))
                task.doTask()
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.canRequestPermission(permission)) {
                        if (showInitialPopup) {
                            val permissionDialog = PermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                permission,
                                PermissionDialog.Mode.INIT,
                                permissionDeniedCallback
                            )
                            permissionDialog.setCanceledOnTouchOutside(setInitialPopupDismissible)
                            permissionDialog.show()
                        } else {
                            activity.requestPermission(permission, object : PermissionCallback {
                                override fun onGrant() {
                                    task.doTask()
                                }

                                override fun onDeny() {
                                    if (showDeniedPopup) {
                                        val permissionDialog = PermissionDialog(
                                            activity,
                                            settingsPermissionLauncher,
                                            task,
                                            permission,
                                            PermissionDialog.Mode.DENIED,
                                            permissionDeniedCallback
                                        )
                                        permissionDialog.setCanceledOnTouchOutside(
                                            setDeniedPopupDismissible
                                        )
                                        permissionDialog.setCancelable(setDeniedPopupDismissible)
                                        permissionDialog.show()
                                    } else {
                                        permissionDeniedCallback?.onDeny()
                                    }
                                }
                            })
                        }
                    } else {
                        if (showDeniedPopup) {
                            val permissionDialog = PermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                permission,
                                PermissionDialog.Mode.DENIED,
                                permissionDeniedCallback
                            )
                            permissionDialog.setCanceledOnTouchOutside(setDeniedPopupDismissible)
                            permissionDialog.setCancelable(setDeniedPopupDismissible)
                            permissionDialog.show()
                        } else {
                            permissionDeniedCallback?.onDeny()
                        }
                    }
                }
            }
        }
    }
}
