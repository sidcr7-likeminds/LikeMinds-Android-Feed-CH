package com.likeminds.feedsx.utils.customview

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.utils.permissions.Permission
import com.likeminds.feedsx.utils.permissions.PermissionCallback
import com.likeminds.feedsx.utils.permissions.SessionPermission
import com.likeminds.feedsx.utils.snackbar.CustomSnackBar
import javax.inject.Inject

//todo add connectivity manager support for no internet
open class BaseAppCompatActivity : AppCompatActivity() {
    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */

    @Inject
    lateinit var snackBar: CustomSnackBar

    private lateinit var sessionPermission: SessionPermission
    private val permissionCallbackSparseArray = SparseArray<PermissionCallback>()

    private var wasNetworkGone = false


    protected open fun drawPrimaryColor(color: Int) {}

    protected open fun drawAdvancedColor(
        headerColor: Int,
        buttonsIconsColor: Int,
        textLinksColor: Int,
    ) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionPermission = SessionPermission(application)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        callBranding()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun callBranding() {
        when {
            BrandingData.currentPrimary != null -> {
                drawPrimaryColor(BrandingData.currentPrimary!!)
                setStatusBarColor(Color.WHITE)
            }
            BrandingData.currentAdvanced != null -> {
                drawAdvancedColor(
                    BrandingData.currentAdvanced!!.first,
                    BrandingData.currentAdvanced!!.second,
                    BrandingData.currentAdvanced!!.third
                )
                setStatusBarColor(BrandingData.getHeaderColor())
            }
            else -> {
                drawPrimaryColor(ContextCompat.getColor(this, R.color.colorPrimary))
                setStatusBarColor(Color.WHITE)
            }
        }
    }

    private fun setStatusBarColor(statusBarColor: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun hasPermission(permission: Permission): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            checkSelfPermission(permission.permissionName) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestPermission(permission: Permission, permissionCallback: PermissionCallback) {
        permissionCallbackSparseArray.put(permission.requestCode, permissionCallback)
        sessionPermission.setPermissionRequest(permission)
        requestPermissions(arrayOf(permission.permissionName), permission.requestCode)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun canRequestPermission(permission: Permission): Boolean {
        return !wasRequestedBefore(permission) ||
                shouldShowRequestPermissionRationale(permission.permissionName)
    }

    private fun wasRequestedBefore(permission: Permission): Boolean {
        return sessionPermission.wasPermissionRequestedBefore(permission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val callback = permissionCallbackSparseArray.get(requestCode, null) ?: return
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