package com.likeminds.feedsx.post.view

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.likeminds.feedsx.databinding.FragmentCreatePostBinding
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.view.MediaPickerActivity
import com.likeminds.feedsx.media.view.MediaPickerActivity.Companion.ARG_MEDIA_PICKER_RESULT
import com.likeminds.feedsx.media.view.MediaPickerActivity.Companion.BROWSE_DOCUMENT
import com.likeminds.feedsx.media.view.MediaPickerActivity.Companion.BROWSE_MEDIA
import com.likeminds.feedsx.post.viewmodel.CreatePostViewModel
import com.likeminds.feedsx.utils.AndroidUtils
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    private val viewModel: CreatePostViewModel by viewModels()

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

    private fun initAddAttachmentsView() {
        binding.layoutAttachFiles.setOnClickListener {
            val extra = MediaPickerExtras.Builder()
                .mediaTypes(listOf(PDF))
                .build()
            val intent = MediaPickerActivity.getIntent(requireContext(), extra)
            documentLauncher.launch(intent)
        }

        binding.layoutAddImage.setOnClickListener {
            val extras = MediaPickerExtras.Builder()
                .mediaTypes(listOf(IMAGE, VIDEO))
                .build()

            val intent = MediaPickerActivity.getIntent(requireContext(), extras)
            galleryLauncher.launch(intent)
        }

        binding.layoutAddVideo.setOnClickListener {
            val extras = MediaPickerExtras.Builder()
                .mediaTypes(listOf(IMAGE, VIDEO))
                .build()

            val intent = MediaPickerActivity.getIntent(requireContext(), extras)
            galleryLauncher.launch(intent)
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
//                    onMediaPicked(result)
                }
            }
        }
    }

//    private fun onMediaPicked(result: MediaPickerResult) {
//        val data =
//            MediaUtils.convertMediaViewDataToSingleUriData(requireContext(), result.medias)
//        if (data.isNotEmpty()) {
//            when {
//                MediaType.isPDF(result.mediaTypes) -> {
//                    showPickDocumentsListScreen(*data.toTypedArray())
//                }
//                else -> {
//                    showPickImagesListScreen(*data.toTypedArray())
//                }
//            }
//        }
//    }

//    private fun showPickImagesListScreen(
//        vararg mediaUris: SingleUriData,
//        saveInCache: Boolean = false,
//        isExternallyShared: Boolean = false,
//        textAlreadyPresent: String? = null,
//    ) {
//        val attachments = if (saveInCache) {
//            AndroidUtils.moveAttachmentToCache(requireContext(), *mediaUris)
//        } else {
//            mediaUris.asList()
//        }
//        if (attachments.isNotEmpty()) {
//            val text = if (!textAlreadyPresent.isNullOrEmpty()) {
//                textAlreadyPresent
//            } else {
//                //TODO: Check this
//                ""
////                memberTagging.replaceSelectedMembers(binding.inputBox.etAnswer.editableText)
//            }
//
//            val arrayList = ArrayList<SingleUriData>()
//            arrayList.addAll(attachments)
//
//            val mediaExtras = MediaExtras.Builder()
//                .mediaScreenType(MEDIA_CONVERSATION_EDIT_SCREEN)
//                .mediaUris(arrayList)
////                .communityName(getChatroomViewData()?.communityName())
////                .searchKey(collabcardDetailExtras.searchKey)
////                .communityId(communityId?.toIntOrNull())
//                .text(text)
//                .isExternallyShared(isExternallyShared)
//                .build()
//
//            val intent =
//                MediaActivity.getIntent(requireContext(), mediaExtras, activity?.intent?.clipData)
//            imageVideoSendLauncher.launch(intent)
//        }
//    }

//    private fun showPickDocumentsListScreen(
//        vararg mediaUris: SingleUriData,
//        saveInCache: Boolean = false,
//        isExternallyShared: Boolean = false,
//        textAlreadyPresent: String? = null,
//    ) {
//        val attachments = if (saveInCache) {
//            AndroidUtils.moveAttachmentToCache(requireContext(), *mediaUris)
//        } else {
//            mediaUris.asList()
//        }
//        val text = if (!textAlreadyPresent.isNullOrEmpty()) {
//            textAlreadyPresent
//        } else {
//            ""
//            //TODO: Check this
////            memberTagging.replaceSelectedMembers(binding.inputBox.etAnswer.editableText)
//        }
//
//        val arrayList = ArrayList<SingleUriData>()
//        arrayList.addAll(attachments)
//
//        val mediaExtras = MediaExtras.Builder()
//            .mediaScreenType(MEDIA_DOCUMENT_SEND_SCREEN)
//            .mediaUris(arrayList)
////            .communityName(getChatroomViewData()?.communityName())
////            .searchKey(collabcardDetailExtras.searchKey)
////            .communityId(communityId?.toIntOrNull())
//            .text(text)
//            .isExternallyShared(isExternallyShared)
//            .build()
//        if (attachments.isNotEmpty()) {
//            val intent =
//                MediaActivity.getIntent(requireContext(), mediaExtras, activity?.intent?.clipData)
//            documentSendLauncher.launch(intent)
//        }
//    }

}