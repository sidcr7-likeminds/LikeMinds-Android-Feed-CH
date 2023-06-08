package com.likeminds.feedsx.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.likeminds.feedsx.media.customviews.WrappedDrawable
import com.likeminds.feedsx.media.model.LocalAppData
import com.likeminds.feedsx.media.model.MediaType
import com.likeminds.feedsx.utils.ViewUtils.dpToPx

object AndroidUtils {

    /**
     * Returns the list of apps which can be used pick images using intent
     * */
    fun getExternalImagePickerApps(context: Context): List<LocalAppData> {
        val intent = getExternalImagePickerIntent()
        return getLocalAppData(context, intent)
    }

    /**
     * Returns the list of apps which can be used pick videos using intent
     * */
    fun getExternalVideoPickerApps(context: Context): List<LocalAppData> {
        val intent = getExternalVideoPickerIntent()
        return getLocalAppData(context, intent)
    }

    /**
     * Returns the list of apps which can be used pick both images and videos using intent
     * */
    fun getExternalMediaPickerApps(context: Context): List<LocalAppData> {
        val intent = getExternalMediaPickerIntent()
        return getLocalAppData(context, intent)
    }

    /**
     * Returns the list of apps with basic information which can be queried for a particular intent
     * */
    private fun getLocalAppData(context: Context, intent: Intent): List<LocalAppData> {
        val packageManager = context.packageManager
        return packageManager.queryIntentActivities(intent, 0)
            .mapIndexedNotNull { index, resolveInfo ->
                val drawable = WrappedDrawable(resolveInfo.loadIcon(packageManager))
                drawable.setBounds(0, 0, dpToPx(50), dpToPx(50))
                LocalAppData(
                    index,
                    resolveInfo.loadLabel(packageManager).toString(),
                    drawable,
                    resolveInfo
                )
            }
    }

    /**
     * Returns the Intent to pick specific mediaTypes files from external storage
     * @param mediaTypes - All the mediaTypes for which intent will be called
     * @param allowMultipleSelect - Specify if multiple media files can be selected
     * @param browseClassName - Specify class package and class name of a specific app which needs to be called
     * */
    fun getExternalPickerIntent(
        mediaTypes: List<String>,
        allowMultipleSelect: Boolean,
        browseClassName: Pair<String, String>?
    ): Intent? {
        val intent = when {
            MediaType.isBothImageAndVideo(mediaTypes) -> {
                getExternalMediaPickerIntent(allowMultipleSelect)
            }
            MediaType.isImage(mediaTypes) -> {
                getExternalImagePickerIntent(allowMultipleSelect)
            }
            MediaType.isVideo(mediaTypes) -> {
                getExternalVideoPickerIntent(allowMultipleSelect)
            }
            else -> null
        }
        if (intent != null && browseClassName != null) {
            intent.setClassName(browseClassName.first, browseClassName.second)
        }
        return intent
    }

    /**
     * Returns the Intent to pick images from external storage
     * */
    private fun getExternalImagePickerIntent(allowMultipleSelect: Boolean = true): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleSelect)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        return intent
    }

    /**
     * Returns the Intent to pick both images and videos from external storage
     * */
    private fun getExternalMediaPickerIntent(allowMultipleSelect: Boolean = true): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleSelect)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        return intent
    }

    /**
     * Returns the Intent to pick videos from external storage
     * */
    private fun getExternalVideoPickerIntent(allowMultipleSelect: Boolean = true): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleSelect)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        return intent
    }

    /**
     * Returns the Intent to pick pdfs from external storage
     * */
    fun getExternalDocumentPickerIntent(allowMultipleSelect: Boolean = true): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleSelect)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        return intent
    }

    /**
     * Shows document for the provided uri
     */
    fun startDocumentViewer(context: Context, uri: Uri) {
        val pdfIntent = Intent(Intent.ACTION_VIEW, uri)
        pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            context.startActivity(pdfIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            ViewUtils.showShortToast(context, "No application found to open this document")
        }
    }
}