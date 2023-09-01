package com.likeminds.feedsx.utils.permissions.util

import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import com.likeminds.feedsx.utils.permissions.model.PermissionExtras
import com.likeminds.feedsx.utils.permissions.view.PermissionDialog

class PermissionManager {

    companion object {
        fun performTaskWithPermissionExtras(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            task: PermissionTask,
            permissionExtras: PermissionExtras,
            showInitialPopup: Boolean,
            showDeniedPopup: Boolean,
            setInitialPopupDismissible: Boolean = false,
            setDeniedPopupDismissible: Boolean = false,
            permissionDeniedCallback: PermissionDeniedCallback? = null,
        ) {
            val permissions = permissionExtras.permissions
            if (activity.hasPermissions(permissions)) {
                task.doTask()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.canRequestPermissions(permissions)) {
                        if (showInitialPopup) {
                            val permissionDialog = PermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                null,
                                PermissionDialog.Mode.INIT,
                                permissionDeniedCallback,
                                permissionExtras
                            )
                            permissionDialog.setCanceledOnTouchOutside(setInitialPopupDismissible)
                            permissionDialog.show()
                        } else {
                            activity.requestMultiplePermissions(
                                permissionExtras,
                                object : PermissionCallback {
                                    override fun onGrant() {
                                        task.doTask()
                                    }

                                    override fun onDeny() {
                                        if (showDeniedPopup) {
                                            val permissionDialog = PermissionDialog(
                                                activity,
                                                settingsPermissionLauncher,
                                                task,
                                                null,
                                                PermissionDialog.Mode.DENIED,
                                                permissionDeniedCallback,
                                                permissionExtras
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
                                null,
                                PermissionDialog.Mode.DENIED,
                                permissionDeniedCallback,
                                permissionExtras
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
                                    showDeniedDialog(
                                        activity,
                                        settingsPermissionLauncher,
                                        showDeniedPopup,
                                        task,
                                        permission,
                                        setDeniedPopupDismissible,
                                        permissionDeniedCallback
                                    )
                                }
                            })
                        }
                    } else {
                        showDeniedDialog(
                            activity,
                            settingsPermissionLauncher,
                            showDeniedPopup,
                            task,
                            permission,
                            setDeniedPopupDismissible,
                            permissionDeniedCallback
                        )
                    }
                }
            }
        }

        private fun showDeniedDialog(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            showDeniedPopup: Boolean,
            task: PermissionTask,
            permission: Permission,
            setDeniedPopupDismissible: Boolean,
            permissionDeniedCallback: PermissionDeniedCallback?,
        ) {
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
