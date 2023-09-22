package com.likeminds.feedsx.utils.permissions.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedDialogPermissionBinding
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import com.likeminds.feedsx.utils.permissions.model.PermissionExtras
import com.likeminds.feedsx.utils.permissions.util.*

class LMFeedPermissionDialog(
    private val activity: BaseAppCompatActivity,
    private val settingsPermissionLauncher: ActivityResultLauncher<Intent>,
    private val task: LMFeedPermissionTask,
    private val LMFeedPermission: LMFeedPermission?,
    private val mode: Mode,
    private val LMFeedPermissionDeniedCallback: LMFeedPermissionDeniedCallback?,
    private val permissionExtras: PermissionExtras? = null
) : Dialog(activity), View.OnClickListener {
    private val dialogPermissionBinding: LmFeedDialogPermissionBinding =
        LmFeedDialogPermissionBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(dialogPermissionBinding.root)

        //set branding to view
        dialogPermissionBinding.imageViewIcon.setBackgroundColor(LMFeedBranding.getButtonsColor())
        dialogPermissionBinding.textViewPositiveButton.setTextColor(LMFeedBranding.getButtonsColor())

        if (LMFeedPermission != null) {
            existingPermissionsDialog()
        } else {
            updatedPermissionsDialog()
        }
    }

    // handles permission dialog as per the existing flow
    private fun existingPermissionsDialog() {
        dialogPermissionBinding.apply {
            if (LMFeedPermission == null) {
                return@apply
            }

            imageViewIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    activity,
                    LMFeedPermission.dialogImage
                )
            )
            when (mode) {
                Mode.INIT -> {
                    textViewMessage.text = LMFeedPermission.preDialogMessage
                    textViewPositiveButton.text =
                        activity.getString(R.string.permission_continue)
                }

                Mode.DENIED -> {
                    textViewMessage.text = LMFeedPermission.deniedDialogMessage
                    textViewPositiveButton.text =
                        activity.getString(R.string.settings)
                }
            }
            textViewPositiveButton.setOnClickListener(this@LMFeedPermissionDialog)
            textViewNegativeButton.setOnClickListener(this@LMFeedPermissionDialog)
            if (LMFeedPermissionDeniedCallback != null) {
                setOnCancelListener { LMFeedPermissionDeniedCallback.onDeny() }
            }
        }
    }

    // handles permission dialog as per the updated flow with [PermissionExtras]
    private fun updatedPermissionsDialog() {
        dialogPermissionBinding.apply {
            if (permissionExtras == null) {
                return@apply
            }

            imageViewIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    activity,
                    permissionExtras.dialogImage
                )
            )
            when (mode) {
                Mode.INIT -> {
                    textViewMessage.text = permissionExtras.preDialogMessage
                    textViewPositiveButton.text =
                        activity.getString(R.string.permission_continue)
                }
                Mode.DENIED -> {
                    textViewMessage.text = permissionExtras.deniedDialogMessage
                    textViewPositiveButton.text =
                        activity.getString(R.string.settings)
                }
            }
            textViewPositiveButton.setOnClickListener {
                positiveButtonClicked()
            }
            textViewNegativeButton.setOnClickListener {
                negativeButtonClicked()
            }
            if (LMFeedPermissionDeniedCallback != null) {
                setOnCancelListener { LMFeedPermissionDeniedCallback.onDeny() }
            }
        }
    }

    // handles positive button click for the new flow
    private fun positiveButtonClicked() {
        if (permissionExtras == null) {
            return
        }
        when (mode) {
            Mode.INIT ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestMultiplePermissions(
                        permissionExtras,
                        object : LMFeedPermissionCallback {
                            override fun onGrant() {
                                task.doTask()
                            }

                            override fun onDeny() {
                                LMFeedPermissionDeniedCallback?.onDeny()
                            }
                        })
                }
            Mode.DENIED -> {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", activity.packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                settingsPermissionLauncher.launch(intent)
            }
        }
        dismiss()
    }

    // handles negative button click for the new flow
    private fun negativeButtonClicked() {
        LMFeedPermissionDeniedCallback?.onCancel()
        dismiss()
    }

    override fun onClick(v: View) {
        if (LMFeedPermission == null) {
            return
        }

        when (v.id) {
            R.id.textViewPositiveButton -> {
                when (mode) {
                    Mode.INIT ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.requestPermission(
                                LMFeedPermission,
                                object : LMFeedPermissionCallback {
                                    override fun onGrant() {
                                        task.doTask()
                                    }

                                    override fun onDeny() {
                                        LMFeedPermissionDeniedCallback?.onDeny()
                                    }
                                })
                        }
                    Mode.DENIED -> {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", activity.packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        settingsPermissionLauncher.launch(intent)
                    }
                }
                dismiss()
            }
            R.id.textViewNegativeButton -> {
                LMFeedPermissionDeniedCallback?.onCancel()
                dismiss()
            }
        }
    }

    enum class Mode {
        INIT, DENIED
    }

    override fun onStart() {
        super.onStart()
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
