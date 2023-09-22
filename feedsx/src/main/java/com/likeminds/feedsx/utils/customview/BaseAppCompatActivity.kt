package com.likeminds.feedsx.utils.customview

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.utils.permissions.*
import com.likeminds.feedsx.utils.permissions.model.PermissionExtras
import com.likeminds.feedsx.utils.permissions.util.*
import com.likeminds.feedsx.utils.snackbar.LMFeedCustomSnackBar
import javax.inject.Inject

open class BaseAppCompatActivity : AppCompatActivity() {
    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older feed_versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */

    @Inject
    lateinit var snackBar: LMFeedCustomSnackBar

    private lateinit var LMFeedSessionPermission: LMFeedSessionPermission
    private val LMFeedPermissionCallbackSparseArray = SparseArray<LMFeedPermissionCallback>()

    private var wasNetworkGone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LMFeedSessionPermission = LMFeedSessionPermission(application)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        setStatusBarColor(LMFeedBranding.getHeaderColor())
    }

    override fun onPause() {
        super.onPause()
    }

    // todo: find alternative for this code
    @Suppress("Deprecation")
    private fun setStatusBarColor(statusBarColor: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
        @RequiresApi(Build.VERSION_CODES.M)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun hasPermission(LMFeedPermission: LMFeedPermission): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            checkSelfPermission(LMFeedPermission.permissionName) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            var hasPermission = true
            permissions.forEach { permission ->
                hasPermission =
                    hasPermission && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
            return hasPermission
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestPermission(
        LMFeedPermission: LMFeedPermission,
        LMFeedPermissionCallback: LMFeedPermissionCallback
    ) {
        LMFeedPermissionCallbackSparseArray.put(
            LMFeedPermission.requestCode,
            LMFeedPermissionCallback
        )
        LMFeedSessionPermission.setPermissionRequest(LMFeedPermission.permissionName)
        requestPermissions(arrayOf(LMFeedPermission.permissionName), LMFeedPermission.requestCode)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestMultiplePermissions(
        permissionExtras: PermissionExtras,
        LMFeedPermissionCallback: LMFeedPermissionCallback
    ) {
        permissionExtras.apply {
            permissions.forEach { permissionName ->
                LMFeedPermissionCallbackSparseArray.put(requestCode, LMFeedPermissionCallback)
                LMFeedSessionPermission.setPermissionRequest(permissionName)
            }
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun canRequestPermission(LMFeedPermission: LMFeedPermission): Boolean {
        return !wasRequestedBefore(LMFeedPermission.permissionName) ||
                shouldShowRequestPermissionRationale(LMFeedPermission.permissionName)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun canRequestPermissions(permissions: Array<String>): Boolean {
        var canRequest = true
        permissions.forEach { permission ->
            canRequest = canRequest && (!wasRequestedBefore(permission) ||
                    shouldShowRequestPermissionRationale(permission))
        }
        return canRequest
    }

    private fun wasRequestedBefore(permissionName: String): Boolean {
        return LMFeedSessionPermission.wasPermissionRequestedBefore(permissionName)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val callback = LMFeedPermissionCallbackSparseArray.get(requestCode, null) ?: return
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onGrant()
            } else {
                callback.onDeny()
            }
        } else {
            callback.onDeny()
        }
    }
}