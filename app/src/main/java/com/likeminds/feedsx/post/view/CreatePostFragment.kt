package com.likeminds.feedsx.post.view

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentCreatePostBinding
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.media.view.MediaPickerActivity
import com.likeminds.feedsx.media.view.MediaPickerActivity.Companion.ARG_MEDIA_PICKER_RESULT
import com.likeminds.feedsx.media.view.MediaPickerActivity.Companion.BROWSE_DOCUMENT
import com.likeminds.feedsx.media.view.MediaPickerActivity.Companion.BROWSE_MEDIA
import com.likeminds.feedsx.post.view.adapter.DocumentsCreatePostAdapter
import com.likeminds.feedsx.post.view.adapter.MultipleMediaCreatePostAdapter
import com.likeminds.feedsx.post.viewmodel.CreatePostViewModel
import com.likeminds.feedsx.utils.AndroidUtils
import com.likeminds.feedsx.utils.ViewDataConverter.convertSingleDataUri
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    private val viewModel: CreatePostViewModel by viewModels()

    private var selectedMediaUris: ArrayList<SingleUriData> = arrayListOf()
    private var multiMediaAdapter: MultipleMediaCreatePostAdapter? = null
    private var documentsAdapter: DocumentsCreatePostAdapter? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.extras?.getParcelable<MediaPickerResult>(ARG_MEDIA_PICKER_RESULT)
                checkMediaPickedResult(data)
            }
        }

    private val documentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.extras?.getParcelable<MediaPickerResult>(ARG_MEDIA_PICKER_RESULT)
                checkMediaPickedResult(data)
            }
        }

    override fun getViewBinding(): FragmentCreatePostBinding {
        return FragmentCreatePostBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initAddAttachmentsView()
    }

    private fun initiateMediaPicker(list: List<String>) {
        val extras = MediaPickerExtras.Builder()
            .mediaTypes(list)
            .allowMultipleSelect(true)
            .build()

        val intent = MediaPickerActivity.getIntent(requireContext(), extras)
        galleryLauncher.launch(intent)
    }

    private fun initAddAttachmentsView() {
        binding.layoutAttachFiles.setOnClickListener {
            val extra = MediaPickerExtras.Builder()
                .mediaTypes(listOf(PDF))
                .allowMultipleSelect(true)
                .build()
            val intent = MediaPickerActivity.getIntent(requireContext(), extra)
            documentLauncher.launch(intent)
        }

        binding.layoutAddImage.setOnClickListener {
            initiateMediaPicker(listOf(IMAGE))
        }

        binding.layoutAddVideo.setOnClickListener {
            initiateMediaPicker(listOf(VIDEO))
        }
    }

    private fun checkMediaPickedResult(result: MediaPickerResult?) {
        if (result != null) {
            when (result.mediaPickerResultType) {
                MEDIA_RESULT_BROWSE -> {
                    if (MediaType.isPDF(result.mediaTypes)) {
                        val intent = AndroidUtils.getExternalDocumentPickerIntent(
                            allowMultipleSelect = result.allowMultipleSelect
                        )
                        startActivityForResult(intent, BROWSE_DOCUMENT)
                    } else {
                        val intent = AndroidUtils.getExternalPickerIntent(
                            result.mediaTypes,
                            result.allowMultipleSelect,
                            result.browseClassName
                        )
                        if (intent != null)
                            startActivityForResult(intent, BROWSE_MEDIA)
                    }
                }
                MEDIA_RESULT_PICKED -> {
                    onMediaPicked(result)
                }
            }
        }
    }

    //TODO: Handle this.
    private fun onMediaPickedFromGallery(data: Intent?) {
        val uris = MediaUtils.getExternalIntentPickerUris(data)
        viewModel.fetchUriDetails(requireContext(), uris) {
            val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                requireContext(), it
            )
            if (mediaUris.isNotEmpty()) {
//                showPickImagesListScreen(*mediaUris.toTypedArray(), saveInCache = true)
            }
        }
    }

    private fun onPdfPicked(data: Intent?) {
        val uris = MediaUtils.getExternalIntentPickerUris(data)
        viewModel.fetchUriDetails(requireContext(), uris) {
            val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                requireContext(), it
            )
            selectedMediaUris.addAll(mediaUris)
            if (mediaUris.isNotEmpty()) {
                showPickDocuments(mediaUris)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            BROWSE_MEDIA -> {
                onMediaPickedFromGallery(data)
            }
            BROWSE_DOCUMENT -> {
                onPdfPicked(data)
            }
        }
    }

    private fun onMediaPicked(result: MediaPickerResult) {
        val data =
            MediaUtils.convertMediaViewDataToSingleUriData(requireContext(), result.medias)
        Log.d("TAG", "onMediaPicked: $data")
        selectedMediaUris.addAll(data)
        if (data.isNotEmpty()) {
            when {
                MediaType.isPDF(result.mediaTypes) -> {
                    showPickDocuments(data)
                }
                selectedMediaUris.size == 1 && MediaType.isImage(result.mediaTypes.first()) -> {
                    showPickImage(data)
                }
                selectedMediaUris.size == 1 && MediaType.isVideo(result.mediaTypes.first()) -> {
                    showPickVideo(data)
                }
                else -> {
                    showMultiMediaAttachments(data)
                }
            }
        }
    }

    private fun handlePostButton() {
        //TODO: handle post clickable or not
    }


    private fun handleAddAttachmentLayouts(show: Boolean) {
        binding.groupAddAttachments.isVisible = show
    }

    private fun showPickDocuments(data: ArrayList<SingleUriData>) {
        Log.d("TAG", "showPickDocuments: " + data + selectedMediaUris.size)
        handleAddAttachmentLayouts(false)
        binding.apply {
            singleVideoAttachment.root.hide()
            singleImageAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.show()
            multipleMediaAttachment.root.hide()
            documentsAttachment.btnAddMore.setOnClickListener {
                initiateMediaPicker(listOf(PDF))
            }

            val attachments = selectedMediaUris.map {
                convertSingleDataUri(it)
            }

            if (documentsAdapter == null) {
                documentsAdapter = DocumentsCreatePostAdapter()
                documentsAttachment.rvDocuments.apply {
                    adapter = documentsAdapter
                    layoutManager = LinearLayoutManager(context)
                }
            }
            documentsAdapter!!.replace(attachments)
        }
    }

    private fun showPickVideo(data: ArrayList<SingleUriData>) {
        handleAddAttachmentLayouts(false)
        binding.apply {
            singleVideoAttachment.root.show()
            singleImageAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.hide()
            singleVideoAttachment.btnAddMore.setOnClickListener {
                initiateMediaPicker(listOf(IMAGE, VIDEO))
            }
            singleVideoAttachment.layoutSingleVideoPost.ivCross.setOnClickListener {
                selectedMediaUris.clear()
                singleVideoAttachment.root.hide()
                handleAddAttachmentLayouts(true)
            }

            //TODO: Use exo player
            singleVideoAttachment.layoutSingleVideoPost.vvSingleVideoPost.setVideoURI(data.first().uri)
        }
    }

    private fun showPickImage(data: ArrayList<SingleUriData>) {
        handleAddAttachmentLayouts(false)
        binding.apply {
            singleImageAttachment.root.show()
            singleVideoAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.hide()
            singleImageAttachment.btnAddMore.setOnClickListener {
                initiateMediaPicker(listOf(IMAGE, VIDEO))
            }
            singleImageAttachment.layoutSingleImagePost.ivCross.setOnClickListener {
                selectedMediaUris.clear()
                singleImageAttachment.root.hide()
                handleAddAttachmentLayouts(true)
            }

            ImageBindingUtil.loadImage(
                singleImageAttachment.layoutSingleImagePost.ivSingleImagePost,
                data.first().uri,
                placeholder = R.drawable.image_placeholder
            )
        }
    }

    private fun showMultiMediaAttachments(data: ArrayList<SingleUriData>) {
        handleAddAttachmentLayouts(false)
        binding.apply {
            singleImageAttachment.root.hide()
            singleVideoAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.show()
            multipleMediaAttachment.btnAddMore.setOnClickListener {
                initiateMediaPicker(listOf(IMAGE, VIDEO))
            }

            val attachments = selectedMediaUris.map {
                convertSingleDataUri(it)
            }

            if (multiMediaAdapter == null) {
                multipleMediaAttachment.viewpagerMultipleMedia.isSaveEnabled = false
                multiMediaAdapter = MultipleMediaCreatePostAdapter()
                multipleMediaAttachment.viewpagerMultipleMedia.adapter = multiMediaAdapter
                multipleMediaAttachment.dotsIndicator.setViewPager2(multipleMediaAttachment.viewpagerMultipleMedia)
            }
            multiMediaAdapter!!.replace(attachments)
        }
    }

}