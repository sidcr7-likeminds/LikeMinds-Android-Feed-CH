package com.likeminds.feedsx.utils.permissions.util

import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import com.likeminds.feedsx.utils.permissions.model.PermissionExtras
import com.likeminds.feedsx.utils.permissions.view.LMFeedPermissionDialog

class LMFeedPermissionManager {

    companion object {
        fun performTaskWithPermissionExtras(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            task: LMFeedPermissionTask,
            permissionExtras: PermissionExtras,
            showInitialPopup: Boolean,
            showDeniedPopup: Boolean,
            setInitialPopupDismissible: Boolean = false,
            setDeniedPopupDismissible: Boolean = false,
            LMFeedPermissionDeniedCallback: LMFeedPermissionDeniedCallback? = null,
        ) {
            val permissions = permissionExtras.permissions
            if (activity.hasPermissions(permissions)) {
                task.doTask()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.canRequestPermissions(permissions)) {
                        if (showInitialPopup) {
                            val LMFeedPermissionDialog = LMFeedPermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                null,
                                LMFeedPermissionDialog.Mode.INIT,
                                LMFeedPermissionDeniedCallback,
                                permissionExtras
                            )
                            LMFeedPermissionDialog.setCanceledOnTouchOutside(
                                setInitialPopupDismissible
                            )
                            LMFeedPermissionDialog.show()
                        } else {
                            activity.requestMultiplePermissions(
                                permissionExtras,
                                object : LMFeedPermissionCallback {
                                    override fun onGrant() {
                                        task.doTask()
                                    }

                                    override fun onDeny() {
                                        if (showDeniedPopup) {
                                            val LMFeedPermissionDialog = LMFeedPermissionDialog(
                                                activity,
                                                settingsPermissionLauncher,
                                                task,
                                                null,
                                                LMFeedPermissionDialog.Mode.DENIED,
                                                LMFeedPermissionDeniedCallback,
                                                permissionExtras
                                            )
                                            LMFeedPermissionDialog.setCanceledOnTouchOutside(
                                                setDeniedPopupDismissible
                                            )
                                            LMFeedPermissionDialog.setCancelable(
                                                setDeniedPopupDismissible
                                            )
                                            LMFeedPermissionDialog.show()
                                        } else {
                                            LMFeedPermissionDeniedCallback?.onDeny()
                                        }
                                    }
                                })
                        }
                    } else {
                        if (showDeniedPopup) {
                            val LMFeedPermissionDialog = LMFeedPermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                null,
                                LMFeedPermissionDialog.Mode.DENIED,
                                LMFeedPermissionDeniedCallback,
                                permissionExtras
                            )
                            LMFeedPermissionDialog.setCanceledOnTouchOutside(
                                setDeniedPopupDismissible
                            )
                            LMFeedPermissionDialog.setCancelable(setDeniedPopupDismissible)
                            LMFeedPermissionDialog.show()
                        } else {
                            LMFeedPermissionDeniedCallback?.onDeny()
                        }
                    }
                }
            }
        }

        fun performTaskWithPermission(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            task: LMFeedPermissionTask,
            LMFeedPermission: LMFeedPermission,
            showInitialPopup: Boolean,
            showDeniedPopup: Boolean,
            setInitialPopupDismissible: Boolean = false,
            setDeniedPopupDismissible: Boolean = false,
            LMFeedPermissionDeniedCallback: LMFeedPermissionDeniedCallback? = null,
        ) {
            if (activity.hasPermission(LMFeedPermission))
                task.doTask()
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.canRequestPermission(LMFeedPermission)) {
                        if (showInitialPopup) {
                            val LMFeedPermissionDialog = LMFeedPermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                LMFeedPermission,
                                LMFeedPermissionDialog.Mode.INIT,
                                LMFeedPermissionDeniedCallback
                            )
                            LMFeedPermissionDialog.setCanceledOnTouchOutside(
                                setInitialPopupDismissible
                            )
                            LMFeedPermissionDialog.show()
                        } else {
                            activity.requestPermission(
                                LMFeedPermission,
                                object : LMFeedPermissionCallback {
                                    override fun onGrant() {
                                        task.doTask()
                                    }

                                    override fun onDeny() {
                                        showDeniedDialog(
                                            activity,
                                            settingsPermissionLauncher,
                                            showDeniedPopup,
                                            task,
                                            LMFeedPermission,
                                            setDeniedPopupDismissible,
                                            LMFeedPermissionDeniedCallback
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
                            LMFeedPermission,
                            setDeniedPopupDismissible,
                            LMFeedPermissionDeniedCallback
                        )
                    }
                }
            }
        }

        private fun showDeniedDialog(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            showDeniedPopup: Boolean,
            task: LMFeedPermissionTask,
            LMFeedPermission: LMFeedPermission,
            setDeniedPopupDismissible: Boolean,
            LMFeedPermissionDeniedCallback: LMFeedPermissionDeniedCallback?,
        ) {
            if (showDeniedPopup) {
                val LMFeedPermissionDialog = LMFeedPermissionDialog(
                    activity,
                    settingsPermissionLauncher,
                    task,
                    LMFeedPermission,
                    LMFeedPermissionDialog.Mode.DENIED,
                    LMFeedPermissionDeniedCallback
                )
                LMFeedPermissionDialog.setCanceledOnTouchOutside(setDeniedPopupDismissible)
                LMFeedPermissionDialog.setCancelable(setDeniedPopupDismissible)
                LMFeedPermissionDialog.show()
            } else {
                LMFeedPermissionDeniedCallback?.onDeny()
            }
        }
    }
}
