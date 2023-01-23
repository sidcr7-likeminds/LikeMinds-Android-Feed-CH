package com.likeminds.feedsx.media.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.media.model.MEDIA_RESULT_BROWSE
import com.likeminds.feedsx.media.model.MediaPickerExtras
import com.likeminds.feedsx.media.model.MediaPickerResult
import com.likeminds.feedsx.media.model.MediaType
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPickerActivity : BaseAppCompatActivity() {

    private lateinit var mediaPickerExtras: MediaPickerExtras

    companion object {
        const val PICK_MEDIA = 5001
        const val BROWSE_MEDIA = 5002
        const val BROWSE_DOCUMENT = 5003
        const val PICK_CAMERA = 5004
        const val CROP_IMAGE = 5005

        private const val ARG_MEDIA_PICKER_EXTRAS = "mediaPickerExtras"
        const val ARG_MEDIA_PICKER_RESULT = "mediaPickerResult"

        fun start(context: Context, extras: MediaPickerExtras) {
            val intent = Intent(context, MediaPickerActivity::class.java)
            intent.apply {
                putExtras(Bundle().apply {
                    putParcelable(ARG_MEDIA_PICKER_EXTRAS, extras)
                })
            }
            context.startActivity(intent)
        }

        fun getIntent(context: Context, extras: MediaPickerExtras): Intent {
            return Intent(context, MediaPickerActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putParcelable(ARG_MEDIA_PICKER_EXTRAS, extras)
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_picker)
    }

    private fun startMediaPickerFragment() {
        checkIfDocumentPickerInitiated()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment ?: return
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_media_picker_graph)
        val navController = navHostFragment.navController

        when {
            MediaType.isImageOrVideo(mediaPickerExtras.mediaTypes) -> {
                navGraph.setStartDestination(R.id.media_picker_folder_fragment)
            }
            MediaType.isPDF(mediaPickerExtras.mediaTypes) -> {
                navGraph.setStartDestination(R.id.media_picker_document_fragment)
            }
            else -> {
                finish()
            }
        }
        val args = Bundle().apply {
            putParcelable(ARG_MEDIA_PICKER_EXTRAS, mediaPickerExtras)
        }
        navController.setGraph(navGraph, args)
    }

    /**
     * If Media Picker type is Pdf and device version is >= Q(29), then show system app picker.
     * This is done due to storage restrictions for non-media files in Android 10.
     * */
    private fun checkIfDocumentPickerInitiated() {
        if (MediaType.isPDF(mediaPickerExtras.mediaTypes)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        ) {
            val intent = Intent().apply {
                putExtras(Bundle().apply {
                    putParcelable(
                        ARG_MEDIA_PICKER_RESULT, MediaPickerResult.Builder()
                            .mediaPickerResultType(MEDIA_RESULT_BROWSE)
                            .mediaTypes(mediaPickerExtras.mediaTypes)
                            .allowMultipleSelect(mediaPickerExtras.allowMultipleSelect)
                            .build()
                    )
                })
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}