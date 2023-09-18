package com.likeminds.feedsx.media.view

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.likeminds.feedsx.databinding.LmFeedFragmentImageCropBinding
import com.likeminds.feedsx.media.model.ImageCropExtras
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.utils.ValueUtils.getMediaType
import com.likeminds.feedsx.utils.file.FileUtil
import kotlinx.coroutines.launch

class LMFeedImageCropFragment : Fragment() {
    companion object {
        const val REQUEST_KEY = "image_edit"
        const val BUNDLE_ARG_URI = "arg_uri"

        const val TAG = "ImageCropFragment"
        private const val BUNDLE_IMAGE_CROP = "bundle of image crop"

        @JvmStatic
        fun getInstance(extras: ImageCropExtras): LMFeedImageCropFragment {
            val fragment = LMFeedImageCropFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_IMAGE_CROP, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var extras: ImageCropExtras

    private lateinit var binding: LmFeedFragmentImageCropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CreatePostFragment", "onCreate: ")
        if (arguments == null) return
        Log.d("CreatePostFragment", "onCreate: 1")
        extras = LMFeedImageCropFragmentArgs.fromBundle(requireArguments()).cropExtras
        Log.d("CreatePostFragment", "onCreate: ${extras.cropHeight}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LmFeedFragmentImageCropBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cropImageView.apply {
            setImageUriAsync(extras.singleUriData?.uri)
            setFixedAspectRatio(true)
            setAspectRatio(extras.cropWidth, extras.cropHeight)
            setMinCropResultSize(480, 270)
            setOnCropImageCompleteListener { imageView, result ->
                lifecycleScope.launch {
                    imageView.context.let {
                        if (result.isSuccessful) {
                            val uri = result.uriContent
                            val imageDimensions =
                                FileUtil.getImageDimensions(requireContext(), uri ?: Uri.EMPTY)
                            setFragmentResult(
                                REQUEST_KEY,
                                bundleOf(
                                    BUNDLE_ARG_URI to SingleUriData.Builder()
                                        .uri(uri ?: Uri.EMPTY)
                                        .fileType(uri.getMediaType(requireContext()) ?: "")
                                        .mediaName(extras.singleUriData?.mediaName)
                                        .format(extras.singleUriData?.format)
                                        .thumbnailAwsFolderPath(extras.singleUriData?.thumbnailAwsFolderPath)
                                        .thumbnailLocalFilePath(extras.singleUriData?.thumbnailLocalFilePath)
                                        .awsFolderPath(extras.singleUriData?.awsFolderPath)
                                        .duration(extras.singleUriData?.duration)
                                        .pdfPageCount(extras.singleUriData?.pdfPageCount)
                                        .size(extras.singleUriData?.size ?: 0)
                                        .width(imageDimensions.first)
                                        .height(imageDimensions.second)
                                        .build()
                                )
                            )
                            findNavController().navigateUp()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Image crop failed: " + result.error?.message,
                                Toast.LENGTH_LONG
                            ).show()
                            cancelCrop()
                        }
                    }
                }
            }
        }

        binding.btnDone.setOnClickListener {
            cropImage()
        }

        binding.btnCancel.setOnClickListener {
            cancelCrop()
        }

        binding.btnRotate.setOnClickListener {
            binding.cropImageView.rotateImage(-90)
        }

    }

    private fun cropImage() {
        val cropImageUri = Uri.fromFile(FileUtil.createImageFile(requireContext()))
        binding.cropImageView.saveCroppedImageAsync(
            cropImageUri,
            Bitmap.CompressFormat.JPEG,
            95,
            0,
            0
        )
    }

    private fun cancelCrop() {
        findNavController().navigateUp()
    }
}