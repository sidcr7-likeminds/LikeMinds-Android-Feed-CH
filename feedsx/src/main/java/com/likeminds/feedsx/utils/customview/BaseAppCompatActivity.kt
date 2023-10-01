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

    private lateinit var lmFeedSessionPermission: LMFeedSessionPermission
    private val lmFeedPermissionCallbackSparseArray = SparseArray<LMFeedPermissionCallback>()

    private var wasNetworkGone = false

    protected open fun attachDagger() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachDagger()
        lmFeedSessionPermission = LMFeedSessionPermission(application)
    }

    override fun onStart() {
        super.onStart()
        setStatusBarColor(LMFeedBranding.getHeaderColor())
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

    fun hasPermission(lmFeedPermission: LMFeedPermission): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            checkSelfPermission(lmFeedPermission.permissionName) == PackageManager.PERMISSION_GRANTED
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
        lmFeedPermission: LMFeedPermission,
        lmFeedPermissionCallback: LMFeedPermissionCallback
    ) {
        lmFeedPermissionCallbackSparseArray.put(
            lmFeedPermission.requestCode,
            lmFeedPermissionCallback
        )
        lmFeedSessionPermission.setPermissionRequest(lmFeedPermission.permissionName)
        requestPermissions(arrayOf(lmFeedPermission.permissionName), lmFeedPermission.requestCode)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestMultiplePermissions(
        permissionExtras: PermissionExtras,
        lmFeedPermissionCallback: LMFeedPermissionCallback
    ) {
        permissionExtras.apply {
            permissions.forEach { permissionName ->
                lmFeedPermissionCallbackSparseArray.put(requestCode, lmFeedPermissionCallback)
                lmFeedSessionPermission.setPermissionRequest(permissionName)
            }
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun canRequestPermission(lmFeedPermission: LMFeedPermission): Boolean {
        return !wasRequestedBefore(lmFeedPermission.permissionName) ||
                shouldShowRequestPermissionRationale(lmFeedPermission.permissionName)
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
        return lmFeedSessionPermission.wasPermissionRequestedBefore(permissionName)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val callback = lmFeedPermissionCallbackSparseArray.get(requestCode, null) ?: return
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